import os
import signal

import threading
import requests, json
import jwt
import requests
from decouple import config
from django.http import JsonResponse
from concurrent.futures import ProcessPoolExecutor
from django.contrib.auth import get_user_model
import datetime

from django import db


class MultiProcessTest:
    PID = os.getpid()

    User = get_user_model()

    def multi_process_test(self, i):
        self.PID = os.getpid()
        db.connections.close_all()
        result = User.objects.filter(last_time__gt=datetime.date(2020, 5, 20))

        print(result)

    
    def run(self):
        _list = ['1', '11', '111', '1111', '11111', '111111']
        with ProcessPoolExecutor(max_workers=3) as pool:
            pool.map(self.multi_process_test, _list, chunksize=200)


    def __del__(self):
        if os.getpid() == 1:
            os.killpg(os.getpgid(self.PID), signal.SIGKILL)


# end = False

# def message(days=7):
#     global end
#     # end를 True로 바꾸면 멈춤
#     if end:
#         return

#     url = 'https://fcm.googleapis.com/fcm/send'
#     data = {
#         # 1명일 때,
#         'to': '', 
#         # 2명 이상일 때,
#         'registration_ids': [],
#         'notification': {
#             'title': '좀',
#             'body': '보내줘!!'
#         }
#     }
#     headers = {
#         'Content-Type': 'application/json',
#         'Authorization': f'key={config("AUTHORIZATION_TOKEN")}'
#     }
#     res = requests.post(url, data=json.dumps(data), headers=headers)
#     result = {
#         'status': res.status_code
#     }

#     threading.Timer(days * 24 * 60 * 60, message, )