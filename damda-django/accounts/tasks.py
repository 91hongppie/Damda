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
from albums.models import Album, Photo
import datetime

from django import db
from celery import shared_task


@app.task
def say_hello():
    print('hello, celery!')


url = 'https://fcm.googleapis.com/fcm/send'
User = get_user_model()

images_for_family = dict()

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
                "body": "오늘은 가족들과 사진 한장 어떠세요?",
                "android_channel_id": "RE-HI"
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
                "body": "오늘은 가족들과 사진 한장 어떠세요?",
                "android_channel_id": "RE-HI"
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

    return result

@app.task
def sendPushNew():
    global images_for_family
    albums = Album.objects.all()
    for album in albums:
        family_id = album.family_id
        album_id = album.id
        # 가족에 대한 기록이 없으면,
        if family_id not in images_for_family.keys():
            images_for_family[family_id] = {
                'album': dict(),
                'NEW': False
            }

        # 가족에 해당 앨범 기록이 남아있으면,
        now = len(Photo.objects.filter(album_id=album_id))
        if album_id in images_for_family[family_id]['album'].keys():
            pre = images_for_family[family_id]['album'][album_id]
            # 현재가 전보다 커졌으면,
            if now > pre:
                images_for_family[family_id]['NEW'] = True
        # 가족에 해당 앨범 기록이 남아있지 않으면,
        else:
            images_for_family[family_id]['NEW'] = True
        images_for_family[family_id]['album'][album_id] = now

    target_list = []

    for family, info in images_for_family.items():
        if info['NEW']:
            target_list.append(family)
    
    User = get_user_model()
    targets = User.objects.filter(family_id__in=target_list)
    devices = Device.objects.filter(owner__in=targets)

    if len(devices) == 1:
        data = {
            "to": f"{devices[0].device_token}",
            "notification": {
                "title": "담다",
                "body": "새로운 사진이 올라왔습니다.",
                "android_channel_id": "NEW"
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
                "body": "새로운 사진이 올라왔습니다.",
                "android_channel_id": "NEW"
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

    for family in images_for_family.keys():
        images_for_family[family]['NEW'] = False

    return result