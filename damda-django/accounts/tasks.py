from __future__ import absolute_import, unicode_literals

import os
import signal

from damda.celery_app import app

import threading
import datetime
import requests, json

import jwt
from decouple import config
from korean_lunar_calendar import KoreanLunarCalendar

from django.contrib.auth import get_user_model
from django import db
from albums.models import Album, Photo
from .models import Device


url = 'https://fcm.googleapis.com/fcm/send'

headers = {
    'Content-Type': 'application/json',
    'Authorization': f'key={config("AUTHORIZATION_TOKEN")}'
}

User = get_user_model()

images_for_family = dict()

@app.task
def sendPushWeekly():
    global url, headers, User, images_for_family
    today = datetime.datetime.now(datetime.timezone.utc)
    week = datetime.timedelta(minutes=1)
    week_ago = today - week
    
    users = User.objects.filter(last_login__lt=week_ago)
    
    devices = Device.objects.filter(switch=True, owner__in=users)

    if len(devices) == 1:
        data = {
            "to": devices[0].device_token,
            "notification": {
                "title": "담다",
                "body": "오늘은 가족들과 사진 한장 어떠세요?",
                "android_channel_id": "RE-HI"
            }
        }
    elif len(devices) > 1:
        device_list = []
        for device in devices:
            device_list.append(device.device_token)

        data = {
            "registration_ids": device_list,
            "notification": {
                "title": "담다",
                "body": "오늘은 가족들과 사진 한장 어떠세요?",
                "android_channel_id": "RE-HI"
            }
        }
    else:
        return '알맞은 대상이 없습니다.'
    
    response = requests.post(url, data=json.dumps(data), headers=headers)
    result = response.status_code

    return result

@app.task
def sendPushCongrat():
    global url, headers, User
    
    KST = datetime.timezone(datetime.timedelta(hours=9))
    today = datetime.datetime.now(tz=KST)
    calendar = KoreanLunarCalendar()
    year = today.year
    month = today.month
    day = today.day

    today_to_lunar = calendar.setSolarDate(year, month, day)

    congrat_users = User.objects.filter(birth=today, is_lunar=False) | User.objects.filter(birth=today_to_lunar, is_lunar=True)

    target_familys = dict()

    devices = []
    me = []
    for user in congrat_users:
        # 생일 대상의 가족들 조회
        congrat_family = User.objects.filter(family_id=user.family_id)
        # 가족들 기기 조회
        for device in Device.objects.filter(owner__in=congrat_family):
            if device.owner == user:
                me.append(device.device_token)
            else:
                devices.append(device.device_token)
        # 알림 보낼 대상 결정
        target_familys[user.first_name] = devices

    result = []

    if len(me) > 0:
        message = "생일 축하드려요~!! 와아아아~!!! (짝짝짝)"
        if len(me) > 1:
            data = {
                "registration_ids": me,
                "notification": {
                    "body": message
                }
            }
        else:
            data = {
                "to": me[0],
                "notification": {
                    "body": message
                }
            }
        response = requests.post(url, data=json.dumps(data), headers=headers)
        result.append(response)
    else:
        return "알맞은 대상이 없습니다."

    for uname, fmem in target_familys.items():
        message = f'빠라밤~! {uname} 님의 생일입니다! 잊지않으셨죠?'
        if len(target_familys[uname]) > 0:
            if len(target_familys[uname]) > 1:
                data = {
                    "registration_ids": fmem,
                    "notification": {
                        "body": message
                    }
                }
            else:
                data = {
                    "to": fmem[0],
                    "notification": {
                        "body": message
                    }
                }
            response = requests.post(url, data=json.dumps(data), headers=headers)
            result.append(response.status_code)
    
    return result