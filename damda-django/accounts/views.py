from django.shortcuts import render
from django.http import HttpResponse
import requests
import json

# Create your views here.
def send_push_notification(ids, title, body):
    url = 'https://fcm.googleapis.com/fcm/send'

    headers = {
        'Authorization': 'key=AIzaSyA0KVyuPdIKaAQkgQqr8yafSSpLKkYKkQA',
        'Content-Type': 'application/json; UTF-8',
    }

    content = {
        'registration_ids': ids,
        'notification': {
            'title': title,
            'body': body
        }
    }

    requests.post(url, data=json.dumps(content), headers=headers)


def notification(request):
    print(request.body)
    send_push_notification('APA91bEBFS7geug932VTU7Xj5VkNuoM3Nr36NspbBVPLzoiWIbsL_KVTx5LxDlRyTlKTwGqIPBPpHlImSVCTvc9i_PkmqRcYaVpEwsQZAK80pbGGonq5TANel_RoIRH1Ez4H8b8suCku', 'Damda', 'textextext')
    return HttpResponse(200)