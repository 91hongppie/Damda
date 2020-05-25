from django.views.decorators.csrf import csrf_exempt
from .models import User, Family, WaitUser, Device
from rest_framework.response import Response
from django.http import JsonResponse
from rest_framework import status
from allauth.socialaccount.providers.kakao.views import KakaoOAuth2Adapter
from rest_auth.registration.views import SocialLoginView
from django.utils.decorators import method_decorator
from django.shortcuts import get_object_or_404
from django.contrib.auth import get_user_model
from rest_framework.permissions import IsAuthenticated, AllowAny
from rest_framework.decorators import api_view, permission_classes, authentication_classes
from rest_framework.response import Response
from rest_framework_jwt.authentication import JSONWebTokenAuthentication
from .serializers import JoinFamilySerializer, FamilySerializer, UserSerializer, UserCreatSerializer, DeviceSerializer
from albums.models import FaceImage
from albums.serializers import EditFaceSerializer
import requests, json
import jwt
from decouple import config

# Create your views here.
@api_view(['GET', 'POST', 'DELETE'])
@permission_classes([IsAuthenticated])
@authentication_classes((JSONWebTokenAuthentication,))
def JoinFamily(request, user_pk):
    if request.method == 'GET':
        data = request.GET.get('req')
        user = get_object_or_404(get_user_model(), pk=user_pk)
        if request.user.state == 1:
            return Response(status=403, data={'error': '요청이 있습니다.'})
        elif data.isdigit():
            family = get_object_or_404(Family, pk=data)
            serializer = JoinFamilySerializer(data={'main_member': family.main_member,'wait_user': user.username})
        else:
            main_user = get_object_or_404(get_user_model(), username=data)
            if main_user.state == 3:
                serializer = JoinFamilySerializer(data={'main_member': main_user.pk,'wait_user': user.username})
            else:
                return Response(status=403, data={'error': '메인 멤버가 아닙니다'})
        if serializer.is_valid(raise_exception=True):
            serializer.save()
            userSerializers = UserSerializer(data={'username': user.username, 'state': 1}, instance=user)
            if userSerializers.is_valid(raise_exception=True):
                userSerializers.save()
        return Response(serializer.data)
    elif request.method == 'POST': 
        User = get_user_model()
        user = get_object_or_404(User, pk=request.POST.get('id'))
        main_member = get_object_or_404(User, pk=user_pk)
        serializer = UserSerializer(data={'state': 2, 'family': main_member.family_id}, instance=user)
        if serializer.is_valid(raise_exception=True):
            serializer.save()
            wait_user = get_object_or_404(User, wait_user=user.username)
            wait_user.delete()
        return Response(serializer.data)
    elif request.method == 'DELETE':
        user = get_object_or_404(get_user_model, pk=user_pk)
        user.delete({'status': 204, 'message': '취소되었습니다.'})
    return Response(status=status.HTTP_400_BAD_REQUEST)


@api_view(['POST'])
@permission_classes([IsAuthenticated])
@authentication_classes((JSONWebTokenAuthentication,))
def MakeFamily(request):
    User = get_user_model()
    user = get_object_or_404(User, username=request.user)
    if not user.family_id:
        if user.state == 1:
            return Response(status=403, data={'error': '요청이 있습니다.'})
        familySerializer = FamilySerializer(data={'main_member': user.id})
        if familySerializer.is_valid(raise_exception=True):
            familySerializer.save()
            userSerializer = UserSerializer(data={'username': user.username, 'state': 3, 'family': familySerializer.data['id']}, instance=user)
            if userSerializer.is_valid(raise_exception=True):
                userSerializer.save()
                return Response(familySerializer.data)
    return Response(status=status.HTTP_400_BAD_REQUEST)

@api_view(['GET', 'POST'])
def GetFamily(request, family_pk):
    if request.method == 'GET':
        users = User.objects.filter(family=family_pk)
        serializer = UserSerializer(users, many=True)
        return Response({'data': serializer.data})
    elif request.method == 'POST':
        face = get_object_or_404(FaceImage, pk=request.data.get('face_id'))
        member_id = request.data.get('member_id')
        if member_id == 0:
            member_id = None
        serializer = EditFaceSerializer(data={'member': member_id}, instance=face)
        if serializer.is_valid():
            serializer.save()
            return Response(serializer.data)
    return Response(status=status.HTTP_400_BAD_REQUEST)

@api_view(['GET'])
@permission_classes([IsAuthenticated])
@authentication_classes((JSONWebTokenAuthentication,))
def UserInfo(request):
    User = get_user_model()
    user = get_object_or_404(User, username=request.user)
    serializer = UserSerializer(user)
    return Response(serializer.data)

@csrf_exempt
def checkemail(request):
    data = request.GET.get('username', None)
    if request.method == 'GET':
        user = User.objects.filter(username=data)        
        if user.count() == 0:
            token = "true"
        else:
            token = "false"
        context = {"token":token}
        return JsonResponse(context)

@api_view(['POST'])
@permission_classes([AllowAny])
@csrf_exempt
def signup(request):
    if request.method == 'POST':
        serializer = UserCreatSerializer(data=request.data)
        if serializer.is_valid(raise_exception=True):
            serializer.save()
            return Response(serializer.data)
        else:
            return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

@method_decorator(csrf_exempt, name='dispatch')
class KakaoLogin(SocialLoginView):
    adapter_class = KakaoOAuth2Adapter


@api_view(['POST'])
@permission_classes([AllowAny])
@csrf_exempt
def addtoken(request):

    device_token = request.body.decode('UTF-8')[6:]
    data = {
        'device_token': device_token
    }
    target = Device.objects.filter(device_token=device_token)
    if len(target) > 0:
        return Response('Re-Hi!', status=status.HTTP_208_ALREADY_REPORTED)
    else:
        serializer = DeviceSerializer(data=data)
        if serializer.is_valid(raise_exception=True):
            serializer.save()
            return Response('Welcome!', status=status.HTTP_201_CREATED)
        else:
            return Response('what!', status=status.HTTP_401_UNAUTHORIZED)


def message(request):
    url = 'https://fcm.googleapis.com/fcm/send'
    data = {
        'to': 'eLILv1MTSP2Dutg1opqIq0:APA91bH0E88fCN8RUMsVTKqcZZJunGoK3jEftVjylN3VZvqQ9vmgxtUx3IDQx7pNSnUBpDIsgdj2mU95HkaFaxCpNiAyOtK23jODr7_yhLThqwOFFgZFPhDwdTydQiHgwfPrutzXqyn0',
        'notification': {
            'title': '좀',
            'body': '보내줘!!'
        }
    }
    headers = {
        'Content-Type': 'application/json',
        'Authorization': f'key={config("AUTHORIZATION_TOKEN")}'
    }
    res = requests.post(url, data=json.dumps(data), headers=headers)
    result = {
        'status': res.status_code
    }
    
    return JsonResponse(result)

