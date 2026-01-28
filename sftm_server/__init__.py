from flask import Flask, render_template, request, session, flash, redirect, g, jsonify, send_from_directory
from werkzeug.security import check_password_hash, generate_password_hash
import functools
import os

# 可选的 AI 依赖 - 如果没有安装，使用 Mock 数据模式
try:
    from ultralytics import YOLO
    from ultralytics.solutions import speed_estimation
    from ultralytics.utils.plotting import Annotator
    HAS_AI = True
except ImportError:
    HAS_AI = False
    print("⚠️  AI 模块未安装，将使用 Mock 数据模式")

try:
    import pafy
    import cv2
    import numpy as np
    from time import time, sleep
    import datetime
    from threading import Thread
    HAS_VIDEO = True
except ImportError:
    HAS_VIDEO = False
    print("⚠️  视频处理模块未安装，将使用 Mock 数据模式")
    import datetime  # 即使没有视频模块，也需要 datetime

from .db import get_db, save_to_traffic_db, save_to_accident_db, init_app
from .mock_data_generator import mock_generator

# Live video url id from youtube
# 多个可用的交通监控视频选项 - 已替换为合适的交通相关视频
TRAFFIC_VIDEOS = [
    'jDDaplaOz7Q',  # 监控点1 (交通监控摄像头 - 高速公路)
    'KYo5jDnk0ls',  # 监控点2 (城市交通监控)
    'C5bLwXkPH1M',  # 监控点3 (交通路口监控)
    'F5Q5ViU8QR0',  # 监控点4 (交通流量监控)
    'M7lc1UVf-VE'   # 监控点5 (交通状况监控)
]

# 当前使用的视频ID (可以切换)
LIVE_URL_ID = TRAFFIC_VIDEOS[0]  # 使用第一个视频

# Model path - 使用相对路径，避免文件不存在错误
MODEL_PATH = os.path.join(os.path.dirname(__file__), '..', 'yolov8n.pt')
ACCIDENT_DET_MODEL_PATH = os.path.join(os.path.dirname(__file__), '..', 'accident_detection.pt')

# Dictionaries
ID2TRAFFICCLASS = {0: 'person', 1: 'bicycle', 2: 'car', 3: 'motorcycle', 5: 'bus', 7: 'truck'}
ID2ACCIDENTCLASS = {0: 'Bike, Bike', 1: 'Bike, Object', 2: 'Bike, Pedestrian', 3: 'Car, Bike', 4:'Car, Car', 5: 'Car, Object', 6: 'Car, Pedestrian'}

# List for use
CONGESTION_LEVELS = ['Low', 'Medium', 'High']

# Find whether a point is at right or left of a line
def find_direction_wrt_line(x, y, p):
    xp_vector = (p[0]-x[0], p[1]-x[1])
    xy_vector = (y[0]-x[0], y[1]-p[1])

    cross_product = (xp_vector[0] * xy_vector[1]) - (xp_vector[1] * xy_vector[0])

    if cross_product > 0:
        direction = -1 # left
    elif cross_product < 0:
        direction = 1 # right

    return direction

# Build a customer speed estimator to get speed for all objects inside the 4 point region
if HAS_AI:
    class CustomSpeedEstimator(speed_estimation.SpeedEstimator):
        def __init__(self):
            super().__init__()
            self.tracking_objs = []

    def calculate_speed(self, trk_id, track, obj_cls):
        """
        Calculation of object speed.

        Args:
            trk_id (int): object track id.
            track (list): tracking history for tracks path drawing
        """

        # Left to AB, BC, CD, DA vector
        if find_direction_wrt_line(self.reg_pts[0], self.reg_pts[1], track[-1]) < 0 and find_direction_wrt_line(self.reg_pts[1], self.reg_pts[2], track[-1]) < 0 and find_direction_wrt_line(self.reg_pts[2], self.reg_pts[3], track[-1]) < 0 and find_direction_wrt_line(self.reg_pts[3], self.reg_pts[0], track[-1]) < 0:
            direction = "known"
        else:
            direction = "unknown"

        if self.trk_previous_times[trk_id] != 0 and direction != "unknown" and trk_id not in self.trk_idslist:
            self.trk_idslist.append(trk_id)

            time_difference = time() - self.trk_previous_times[trk_id]
            if time_difference > 0:
                dist_difference = np.abs(track[-1][1] - self.trk_previous_points[trk_id][1])
                speed = dist_difference / time_difference
                self.dist_data[trk_id] = speed
                self.tracking_objs.append({'id': trk_id, 'class': obj_cls, 'speed': speed})
                

        self.trk_previous_times[trk_id] = time()
        self.trk_previous_points[trk_id] = track[-1]

    def estimate_speed(self, im0, tracks, region_color=(255, 0, 0)):
        """
        Calculate object based on tracking data.

        Args:
            im0 (nd array): Image
            tracks (list): List of tracks obtained from the object tracking process.
            region_color (tuple): Color to use when drawing regions.
        """
        self.im0 = im0
        if tracks[0].boxes.id is None:
            if self.view_img and self.env_check:
                self.display_frames()
            return im0
        self.extract_tracks(tracks)

        self.annotator = Annotator(self.im0, line_width=2)
        self.annotator.draw_region(reg_pts=self.reg_pts, color=region_color, thickness=self.region_thickness)

        for box, trk_id, cls in zip(self.boxes, self.trk_ids, self.clss):
            track = self.store_track_info(trk_id, box)

            if trk_id not in self.trk_previous_times:
                self.trk_previous_times[trk_id] = 0

            self.plot_box_and_track(trk_id, box, cls, track)
            self.calculate_speed(trk_id, track, cls)

        if self.view_img and self.env_check:
            self.display_frames()

        return im0


def set_traffic_info():
    # {class_name: [count, average_speed]}
    return {'person': [0, 0], 'car': [0, 0], 'bicycle': [0, 0], 'bus': [0, 0], 'motorcycle': [0, 0], 'truck': [0, 0]}

def convertBinary(img):
    if not HAS_VIDEO:
        # 如果没有 cv2，返回空字节数据
        return b''
    r = 50.0 / img.shape[0]
    dim = (int(img.shape[1] * r), 50)
    bin_img = cv2.resize(img, dim)
    bin_img = cv2.imencode('.jpg', bin_img)[1].tobytes()
    return bin_img

def save_traffic_information(url, app_context):
    """保存交通信息到数据库"""
    print(f"FETCHING TRAFFIC DATA FROM {url}")
    print("SAVING TRAFFIC INFORMATION INTO DATABASE ...")

    app_context.push()

    # 检查是否有 AI 模块
    if not HAS_AI or not HAS_VIDEO:
        print("📊 AI 模块未安装，使用模拟数据生成器...")
        use_mock_data = True
        model = None
        accident_detector = None
        cap = None
    else:
        # 检查模型文件是否存在
        model_exists = os.path.exists(MODEL_PATH)
        accident_model_exists = os.path.exists(ACCIDENT_DET_MODEL_PATH)

        # Try to get the models, fallback to mock data if failed
        try:
            if model_exists:
                model = YOLO(MODEL_PATH)
            else:
                print(f"⚠️  模型文件不存在: {MODEL_PATH}")
                raise FileNotFoundError("YOLO model not found")

            if accident_model_exists:
                accident_detector = YOLO(ACCIDENT_DET_MODEL_PATH)
            else:
                print(f"⚠️  事故检测模型文件不存在: {ACCIDENT_DET_MODEL_PATH}")
                accident_detector = None

            # Try to open the video using pafy and OpenCV
            try:
                video_url = pafy.new(url).getbest(preftype="mp4").url
                cap = cv2.VideoCapture(video_url)
                if not cap.isOpened():
                    raise Exception(f"Failed to open {video_url}")
            except Exception as e:
                print(f"⚠️  无法访问视频流: {e}")
                print("🔄 切换到模拟数据模式...")
                use_mock_data = True
            else:
                use_mock_data = False
        except Exception as e:
            print(f"⚠️  模型加载失败: {e}")
            print("🔄 切换到模拟数据模式...")
            use_mock_data = True
            cap = None
            model = None
            accident_detector = None

    if use_mock_data:
        # 使用模拟数据模式
        print("📊 使用模拟数据生成器...")
        traffic_info = set_traffic_info()
        is_new_accident = True
        last_accident_time = 0
        
        while True:
            # 生成模拟交通数据
            mock_traffic = mock_generator.generate_traffic_data()
            
            # 更新交通信息
            for vehicle_type, (count, speed) in mock_traffic.items():
                if vehicle_type in traffic_info:
                    traffic_info[vehicle_type][0] = count
                    traffic_info[vehicle_type][1] = speed
            
            # 生成模拟事故数据
            accident_data = mock_generator.generate_accident_data()
            if accident_data and is_new_accident:
                print("🚨 模拟事故检测到!!")
                # 创建一个简单的模拟图像（如果可用）
                if HAS_VIDEO:
                    mock_img = np.zeros((100, 100, 3), dtype=np.uint8)
                    img_data = convertBinary(mock_img)
                else:
                    img_data = b''
                save_to_accident_db({
                    'img': img_data,
                    'involved': accident_data['involved'],
                })
                is_new_accident = False
                last_accident_time = time()
            
            # 检查是否可以报告新事故（15分钟后）
            current_time = time()
            if abs(current_time - last_accident_time) > 900:
                is_new_accident = True
            
            # 保存交通信息到数据库
            save_to_traffic_db(traffic_info=traffic_info)
            
            # 重置交通信息
            traffic_info = set_traffic_info()
            
            # 等待2秒
            sleep(2)
    else:
        # 使用真实视频数据模式
        try:
            if not HAS_AI:
                raise Exception("AI 模块未安装")
            w, h = cap.get(3), cap.get(4)
            region_pts = [(w*0.1, h*0.55), (w*0.25, h*0.8), (w*0.99, h*0.75), (w*0.99, h*0.5)]
            speed_obj = CustomSpeedEstimator()
            speed_obj.set_args(reg_pts=region_pts,
                            names=model.names)
            
            traffic_info = set_traffic_info()
            
            # Whether the accident is new or not based on time
            is_new_accident = True
            last_accident_time = 0

            # Process each frame of the video
            while True:
                # Get the current video frame
                ret, original_frame = cap.read()
                if not ret:
                    print("⚠️  视频流结束，切换到模拟数据模式...")
                    break
                    
                frame = original_frame.copy()
                
                # Track the objects in the video for particular classes we are interested in
                tracks = model.track(source=frame, tracker="bytetrack.yaml", classes=[0, 1, 2, 3, 5, 7], persist=True, imgsz=640, show=False, verbose=False)

                # Estimate speed using ultralytics speed estimator
                speed_obj.estimate_speed(frame, tracks)
                
                # Detect accidents using the model for particular classes
                if accident_detector:
                    try:
                        result = accident_detector(source=frame, imgsz=640, verbose=False)
                        bboxes = result[0].boxes
                        
                        current_time = time() # Save the current time for checking new accidents
                        
                        if len(bboxes.conf) > 0:
                            ind = np.argmax(bboxes.conf)
                                
                            # If the confidence score is more than 80% and the accident is a new accident more than 15 mins after the previous one
                            if bboxes.conf[ind] > 0.85 and is_new_accident:
                                print("A new accident is detected!!")
                                save_to_accident_db({
                                    'img': convertBinary(original_frame) if HAS_VIDEO else b'',
                                    'involved': ID2ACCIDENTCLASS[int(bboxes.cls[ind])], 
                                })
                                is_new_accident = False
                                last_accident_time = time() # Save the time of the accident
                        
                        # If time difference with previous accident is more than 15 mins then the next accident should be taken into account
                        if abs(current_time - last_accident_time) > 900:
                            is_new_accident = True
                    except Exception as e:
                        print(f"⚠️  事故检测失败: {e}")
                
                # Calculate the total no of objects from each class and sum of speeds
                for tracking_obj in speed_obj.tracking_objs:
                    obj_cls, obj_speed = tracking_obj['class'], tracking_obj['speed']
                    if int(obj_cls) in ID2TRAFFICCLASS:
                        traffic_info[ID2TRAFFICCLASS[int(obj_cls)]][0] += 1
                        traffic_info[ID2TRAFFICCLASS[int(obj_cls)]][1] += obj_speed
                
                # Calculate the average speed
                for key, value in traffic_info.items():
                    if value[0]:
                        traffic_info[key][1] = value[1] / value[0]

                # Save the traffic information in the database
                save_to_traffic_db(traffic_info=traffic_info)

                # Reset the traffic volume for next iteration
                traffic_info = set_traffic_info()
                
                # Sleep for 2 secs
                sleep(2)
        except Exception as e:
            print(f"⚠️  视频处理失败: {e}")
            print("🔄 切换到模拟数据模式...")
            # 如果视频处理失败，切换到模拟数据模式
            traffic_info = set_traffic_info()
            is_new_accident = True
            last_accident_time = 0
            
            while True:
                # 生成模拟交通数据
                mock_traffic = mock_generator.generate_traffic_data()
                
                # 更新交通信息
                for vehicle_type, (count, speed) in mock_traffic.items():
                    if vehicle_type in traffic_info:
                        traffic_info[vehicle_type][0] = count
                        traffic_info[vehicle_type][1] = speed
                
                # 生成模拟事故数据
                accident_data = mock_generator.generate_accident_data()
                if accident_data and is_new_accident:
                    print("🚨 模拟事故检测到!!")
                    # 创建一个简单的模拟图像
                    mock_img = np.zeros((100, 100, 3), dtype=np.uint8)
                    save_to_accident_db({
                        'img': convertBinary(mock_img), 
                        'involved': accident_data['involved'], 
                    })
                    is_new_accident = False
                    last_accident_time = time()
                
                # 检查是否可以报告新事故（15分钟后）
                current_time = time()
                if abs(current_time - last_accident_time) > 900:
                    is_new_accident = True
                
                # 保存交通信息到数据库
                save_to_traffic_db(traffic_info=traffic_info)
                
                # 重置交通信息
                traffic_info = set_traffic_info()
                
                # 等待2秒
                sleep(2)

def save_accident_img(img):
    arr_np = np.frombuffer(img, np.uint8)
    img_np = cv2.imdecode(arr_np, cv2.IMREAD_COLOR)
    cv2.imwrite('static/images/temp.jpg', img_np)

def create_app(test_config=None):
    """创建Flask应用实例"""
    # create and configure the app
    app = Flask(__name__, instance_relative_config=True)
    app.config.from_mapping(
        SECRET_KEY=os.environ.get('SECRET_KEY', 'dev-key-change-in-production'),
        DATABASE=os.path.join(app.instance_path, 'sftm.sqlite'),
        TEMPLATES_AUTO_RELOAD=True,  # 模板自动重载
        SEND_FILE_MAX_AGE_DEFAULT=0,  # 静态文件不缓存
        JSON_AS_ASCII=False,  # 支持中文JSON
        JSONIFY_PRETTYPRINT_REGULAR=True,  # 美化JSON输出
    )

    if test_config is None:
        # load the instance config, if it exists, when not testing
        app.config.from_pyfile('config.py', silent=True)
    else:
        # load the test config if passed in
        app.config.from_mapping(test_config)

    # ensure the instance folder exists
    try:
        os.makedirs(app.instance_path)
    except OSError:
        pass

    @app.errorhandler(404)
    def not_found(error):
        return render_template('base.html'), 404
    
    @app.errorhandler(500)
    def internal_error(error):
        return render_template('base.html'), 500

    @app.route('/')
    def index():
        """主页"""
        return render_template("base.html")

    @app.route('/register', methods=('GET', 'POST'))
    def register():
        if request.method == 'POST':
            username = request.form['username']
            password = request.form['password']
            db = get_db()
            error = None

            if not username:
                error = '用户名是必需的。'
            elif not password:
                error = '密码是必需的。'

            if error is None:
                try:
                    db.execute(
                        "INSERT INTO user (username, password) VALUES (?, ?)",
                        (username, generate_password_hash(password)),
                    )
                    db.commit()
                except db.IntegrityError:
                    error = f"用户 {username} 已经注册。"
                else:
                    return render_template('landing.html')

            flash(error)

        return render_template('register.html')

    @app.route('/login', methods=('GET', 'POST'))
    def login():
        if request.method == 'POST':
            username = request.form['username']
            password = request.form['password']
            db = get_db()
            error = None
            user = db.execute(
                'SELECT * FROM user WHERE username = ?', (username,)
            ).fetchone()

            if user is None:
                error = '用户名不正确。'
            elif not check_password_hash(user['password'], password):
                error = '密码不正确。'

            if error is None:
                session.clear()
                session['user_id'] = user['id']
                return redirect('/landing')

            flash(error)

        return render_template('login.html')

    @app.before_request
    def load_logged_in_user():
        user_id = session.get('user_id')

        if user_id is None:
            g.user = None
        else:
            g.user = get_db().execute(
                'SELECT * FROM user WHERE id = ?', (user_id,)
            ).fetchone()
            g.video_id = LIVE_URL_ID

    def login_required(view):
        @functools.wraps(view)
        def wrapped_view(**kwargs):
            if g.user is None:
                return redirect('/login')

            return view(**kwargs)
        return wrapped_view

    @app.route('/landing')
    @login_required
    def landing_page():
        return render_template('landing.html')

    @app.route('/dashboard')
    @login_required
    def dashboard():
        return render_template('dashboard.html')

    @app.route('/traffic_analysis')
    @login_required
    def traffic_analysis():
        return render_template('traffic_analysis.html')


    @app.route('/get_div_data')
    @login_required
    def get_div_data():
        data = [
            {
                'amount': 0, 
                'percentage': 0,
                'change': ''
            }, 
            {
                'amount': 0, 
                'percentage': 0,
                'change': ''
            }, 
            {
                'congestion-level': 'Low', 
                'change': ''
            }
        ]

        db = get_db()
        today_traffic = db.execute(
            '''SELECT ROUND(AVG(pedestrian_count+car_count+bus_count+bicycle_count+motorcycle_count+truck_count)) AS avg_traffic_count, ROUND(AVG(volume)) AS avg_volume, MAX(congestion) AS overall_congestion 
            FROM traffic 
            WHERE date_time LIKE (?)''', (datetime.date.today().strftime("%Y-%m-%d")+'%', )).fetchone()
        
        yesterday_date = datetime.date.today()-datetime.timedelta(days=1)
        yesterday_traffic = db.execute(
            '''SELECT ROUND(AVG(pedestrian_count+car_count+bus_count+bicycle_count+motorcycle_count+truck_count)) AS avg_traffic_count,ROUND(AVG(volume)) AS avg_volume, MAX(congestion) AS overall_congestion 
            FROM traffic 
            WHERE date_time LIKE (?)''', (yesterday_date.strftime("%Y-%m-%d")+'%', )).fetchone()
        
        if today_traffic['avg_volume']:
            data[0]['amount'] = today_traffic['avg_volume']
            data[1]['amount'] = today_traffic['avg_traffic_count']
            data[2]['congestion-level'] = CONGESTION_LEVELS[today_traffic['overall_congestion']]
            if yesterday_traffic['avg_volume']:
                data[0]['percentage'] = round(((today_traffic['avg_volume'] - yesterday_traffic['avg_volume']) / yesterday_traffic['avg_volume']) * 100, 1)
                data[0]['change'] = 'increase' if data[0]['percentage'] > 0 else 'decrease'
                
                data[1]['percentage'] = round(((today_traffic['avg_traffic_count'] - yesterday_traffic['avg_traffic_count']) / yesterday_traffic['avg_traffic_count']) * 100, 1)
                data[1]['change'] = 'increase' if data[1]['percentage'] > 0 else 'decrease'
                
                data[2]['change'] = 'Higher than yesterday' if today_traffic['overall_congestion'] > yesterday_traffic['overall_congestion'] else 'Lower or equal to yesterday'
            else:
                data[0]['percentage'] = 100
                data[0]['change'] = 'increase'
                
                data[1]['percentage'] =100
                data[1]['change'] = 'increase'
                
                data[2]['change'] = 'Higher than yesterday'
        
        print(data)
        return data

    @app.route('/line_chart_data', defaults={'daterange': f"{datetime.date.today()}--{datetime.date.today()}"})
    @app.route('/line_chart_data/<daterange>')
    @login_required
    def line_chart_data(daterange):
        db = get_db()
        
        dates = [datetime.datetime.strptime(str_date, '%Y-%m-%d') for str_date in daterange.split("--")]
        series_data = [
            {
                'name': 'Pedestrian', 
                'data': []
            }, 
            {
                'name': 'Car', 
                'data': []
            }, 
            {
                'name': 'Bus', 
                'data': []
            }, 
            {
                'name': 'Bicycle', 
                'data': []
            }, 
            {
                'name': 'Motorcycle', 
                'data': []
            }, 
            {
                'name': 'Truck', 
                'data': []
            }, 
            {
                'name': 'Traffic Volume', 
                'data': []
            }
        ]
        
        for hour in range(24):
            def get_avg_in_db(string):
                data = db.execute(
                    f"SELECT ROUND(AVG({string})) AS avg_{string} FROM traffic WHERE (date_time BETWEEN datetime(?) AND datetime(?)) AND (date_time LIKE (?))", (dates[0].strftime("%Y-%m-%d"), (dates[1]+datetime.timedelta(days=1)).strftime("%Y-%m-%d"), f'%-%-% {hour:0>2}:%', )
                ).fetchone()
                return data[f'avg_{string}'] if data[f'avg_{string}'] else 0
            
            series_data[0]['data'].append(get_avg_in_db('pedestrian_count'))
            series_data[1]['data'].append(get_avg_in_db('car_count'))
            series_data[2]['data'].append(get_avg_in_db('bus_count'))
            series_data[3]['data'].append(get_avg_in_db('bicycle_count'))
            series_data[4]['data'].append(get_avg_in_db('motorcycle_count'))
            series_data[5]['data'].append(get_avg_in_db('truck_count'))
            series_data[6]['data'].append(get_avg_in_db('volume'))
            
        categories = [f'{hour:0>2}:00' for hour in range(24)]

        print([{
            'series_data': series_data, 
            'categories': categories, 
        }])

        return jsonify([{
            'series_data': series_data, 
            'categories': categories, 

        }])

    @login_required
    @app.route('/angular_chart_data', defaults={'daterange': f"{datetime.date.today()}--{datetime.date.today()}"})
    @app.route('/angular_chart_data/<daterange>')
    def angular_chart_data(daterange):
        dates = [datetime.datetime.strptime(str_date, '%Y-%m-%d') for str_date in daterange.split("--")]
        
        data = [{
                    'value': [],
                    'name': 'Average Speed'
                },
                {
                    'value': [],
                    'name': 'Total Count'
                }]
        
        db = get_db()
        
        def get_avg_in_db(string):
            data = db.execute(
                f"SELECT ROUND(AVG({string})) AS avg_{string}                 FROM traffic WHERE (date_time BETWEEN datetime(?) AND datetime(?))", (dates[0].strftime("%Y-%m-%d"), (dates[1]+datetime.timedelta(days=1)).strftime("%Y-%m-%d"), )
            ).fetchone()
            return data[f'avg_{string}'] if data[f'avg_{string}'] else 0
            
        objs = ['pedestrian', 'car', 'bus', 'bicycle', 'motorcycle', 'truck']
        for ob in objs:
            data[0]['value'].append(get_avg_in_db(f'{ob}_speed'))
            data[1]['value'].append(get_avg_in_db(f'{ob}_count'))
        
        print(data)
        return jsonify(data)

    @app.route('/pie_chart_data', defaults={'daterange': f"{datetime.date.today()}--{datetime.date.today()}"})
    @app.route('/pie_chart_data/<daterange>')
    @login_required
    def pie_chart_data(daterange):
        dates = [datetime.datetime.strptime(str_date, '%Y-%m-%d') for str_date in daterange.split("--")]
        
        data = [{
                'value': 0,
                'name': 'Pedestrian'
                },
                {
                    'value': 0,
                    'name': 'Car'
                },
                {
                    'value': 0,
                    'name': 'Bus'
                },
                {
                    'value': 0,
                    'name': 'Bicycle'
                },
                {
                    'value': 0,
                    'name': 'Motorcycle'
                }, 
                {
                    'value': 0,
                    'name': 'Truck'
                }]
        
        db = get_db()
        def get_volume_in_db(string):
            data = db.execute(
                f"SELECT ROUND(AVG({string}_count) * AVG({string}_speed)) AS {string}_volume FROM traffic WHERE date_time BETWEEN datetime(?) AND datetime(?)", (dates[0].strftime("%Y-%m-%d"), (dates[1]+datetime.timedelta(days=1)).strftime("%Y-%m-%d"), )
            ).fetchone()
            return data[f'{string}_volume'] if data[f'{string}_volume'] else 0
        
        objs = ['pedestrian', 'car', 'bus', 'bicycle', 'motorcycle', 'truck']
        for i, ob in enumerate(objs):
            data[i]['value'] = get_volume_in_db(ob)
        
        print(data)
        return jsonify(data)

    @app.route('/highest_traffic', defaults={'daterange': f"{datetime.date.today()}--{datetime.date.today()}"})
    @app.route('/highest_traffic/<daterange>')
    def highest_traffic(daterange):
        dates = [datetime.datetime.strptime(str_date, '%Y-%m-%d') for str_date in daterange.split("--")]
        
        data = []
        db = get_db()
        highest_traffic_rows = db.execute(
            '''SELECT strftime ('%H', date_time) AS hour_day, ROUND(AVG(volume), 2) as volume, ROUND(AVG(pedestrian_speed+car_speed+bus_speed+bicycle_speed+motorcycle_speed+truck_speed)) AS avg_speed, 
            CASE MAX(AVG(pedestrian_count), AVG(car_count), AVG(bus_count), AVG(bicycle_count), AVG(motorcycle_count), AVG(truck_count))
                WHEN AVG(pedestrian_count)
                    THEN 'Pedestrian'
                WHEN  AVG(car_count)
                    THEN 'Car'
                WHEN  AVG(bus_count)
                    THEN 'Bus'
                WHEN  AVG(bicycle_count)
                    THEN 'Bicycle'
                WHEN  AVG(motorcycle_count)
                    THEN 'Motorcycle'
                WHEN  AVG(truck_count)
                    THEN 'Truck'
                END highest_vehicle
            FROM traffic 
            GROUP BY strftime ('%H', date_time)
            HAVING date_time BETWEEN datetime(?) AND datetime(?)
            ORDER BY volume DESC
            LIMIT 5''', (dates[0].strftime("%Y-%m-%d"), (dates[1]+datetime.timedelta(days=1)).strftime("%Y-%m-%d"), )).fetchall()
                
        for highest_row in highest_traffic_rows:
            data.append(
                {
                    'time': highest_row['hour_day'], 
                    'volume': highest_row['volume'], 
                    'speed': highest_row['avg_speed'], 
                    'highest-vehicle': highest_row['highest_vehicle']
                }
            )
            
        print(data)
        return data

    @app.route('/static/images/<filename>')
    def serve_img(filename):
        return send_from_directory('static/images', filename)

    @app.route('/get_modal_data/<string:acc_id>')
    @login_required
    def get_modal_data(acc_id):
        modal_data = {}
        
        db = get_db()
        data = db.execute(
            "SELECT acc_id, date_time, img, involved, severity, stat FROM accidents WHERE acc_id=?", (acc_id, )
        ).fetchone()
        
        modal_data['id'] = data['acc_id']
        modal_data['time'] = data['date_time']
        modal_data['involved'] = data['involved']
        modal_data['severity'] = data['severity']
        modal_data['stat'] = data['stat']
        
        save_accident_img(data['img'])
        
        print(modal_data)
        return jsonify([modal_data])

    @app.route('/get_accident_data', defaults={'daterange': f"{datetime.date.today()}--{datetime.date.today()}"})
    @app.route('/get_accident_data/<daterange>')
    @login_required
    def get_accident_data(daterange):
        dates = [datetime.datetime.strptime(str_date, '%Y-%m-%d') for str_date in daterange.split("--")]
        accidents = []
        db = get_db()
        data = db.execute(
            "SELECT acc_id, date_time, involved, severity, stat FROM accidents WHERE date_time BETWEEN datetime(?) AND datetime(?)", (dates[0].strftime("%Y-%m-%d"), (dates[1]+datetime.timedelta(days=1)).strftime("%Y-%m-%d"), )).fetchall()
        
        for row in data:
            accidents.append(
                {
                    'id': row['acc_id'], 
                    'time': row['date_time'], 
                    'involved': row['involved'], 
                    'severity': row['severity'], 
                    'stat': row['stat']
                }
            )
        
        print(accidents)
        return accidents

    @app.route('/logout')
    def logout():
        session.clear()
        return redirect('/')

    # 事件管理相关路由
    @app.route('/incident_management')
    @login_required
    def incident_management():
        return render_template('incident_management.html')

    @app.route('/api/incidents', methods=['GET'])
    @login_required
    def get_incidents():
        """获取事件列表"""
        db = get_db()
        incidents = db.execute(
            '''SELECT id, type, level, location, status, occurred_at, source, resolved_at, created_at
            FROM incidents 
            ORDER BY created_at DESC'''
        ).fetchall()
        
        result = []
        for incident in incidents:
            result.append({
                'id': incident['id'],
                'type': incident['type'],
                'level': incident['level'],
                'location': incident['location'],
                'status': incident['status'],
                'occurred_at': incident['occurred_at'],
                'source': incident['source'],
                'resolved_at': incident['resolved_at'],
                'created_at': incident['created_at']
            })
        
        return jsonify(result)

    @app.route('/api/incidents', methods=['POST'])
    @login_required
    def create_incident():
        """创建新事件"""
        data = request.get_json()
        
        db = get_db()
        cursor = db.execute(
            '''INSERT INTO incidents (type, level, location, status, occurred_at, source, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)''',
            (
                data.get('type', 'TRAFFIC_ACCIDENT'),
                data.get('level', 'MEDIUM'),
                data.get('location', ''),
                'PENDING',
                datetime.datetime.now(),
                data.get('source', 'MANUAL'),
                datetime.datetime.now(),
                datetime.datetime.now()
            )
        )
        db.commit()
        
        return jsonify({'id': cursor.lastrowid, 'message': '事件创建成功'})

    @app.route('/api/incidents/<int:incident_id>/resolve', methods=['POST'])
    @login_required
    def resolve_incident(incident_id):
        """解决事件"""
        data = request.get_json()
        
        db = get_db()
        db.execute(
            '''UPDATE incidents 
            SET status = ?, resolved_at = ?, updated_at = ?
            WHERE id = ?''',
            ('RESOLVED', datetime.datetime.now(), datetime.datetime.now(), incident_id)
        )
        db.commit()
        
        return jsonify({'message': '事件已解决'})

    @app.route('/api/incidents/<int:incident_id>/assign', methods=['POST'])
    @login_required
    def assign_incident(incident_id):
        """分配事件"""
        data = request.get_json()
        
        db = get_db()
        db.execute(
            '''UPDATE incidents 
            SET status = ?, updated_at = ?
            WHERE id = ?''',
            ('IN_PROGRESS', datetime.datetime.now(), incident_id)
        )
        db.commit()
        
        return jsonify({'message': '事件已分配'})

    init_app(app)

    if not app.config.get('TRAFFIC_MONITORING_STARTED', False):
        app.config['TRAFFIC_MONITORING_STARTED'] = True
        # Start the object monitoring and saving on a new thread
        thread = Thread(target=save_traffic_information, args=[f"https://www.youtube.com/watch?v={LIVE_URL_ID}", app.app_context()], daemon=True)
        thread.start()

    return app