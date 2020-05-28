from __future__ import absolute_import, unicode_literals

from damda.celery import app

import os
import signal

import threading
import requests, json
import jwt
import requests
from decouple import config
from django.contrib.auth import get_user_model
from .models import Device
import datetime

from django import db
from celery import shared_task


@app.task
def say_hello():
    print('hello, celery!')


url = 'https://fcm.googleapis.com/fcm/send'
User = get_user_model()

@app.task
def sendPushWeekly():
    global url
    today = datetime.datetime.now(datetime.timezone.utc)
    week = datetime.timedelta(minutes=1)
    week_ago = today - week
    
    users = User.objects.filter(last_login__lt=week_ago)
    
    devices = Device.objects.filter(switch=True, owner__in=users)

    if len(devices) == 1:
        data = {
            "to": f"{devices[0].device_token}",
            "notification": {
                "title": "담다",
                "body": "오늘은 가족들과 사진 한장 어떠세요?"
            }
        }
    elif len(devices) > 1:
        dl = []
        for device in devices:
            dl.append(device.device_token)

        data = {
            "registration_ids": f"{dl}",
            "notification": {
                "title": "담다",
                "body": "오늘은 가족들과 사진 한장 어떠세요?"
            }
        }
    else:
        return '알맞은 대상이 없습니다.'

    headers = {
        'Content-Type': 'application/json',
        'Authorization': f'key={config("AUTHORIZATION_TOKEN")}'
    }
    
    response = requests.post(url, data=json.dumps(data), headers=headers)
    result = response.status_code
    print(response)
    return result
