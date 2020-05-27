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
from .serializers import JoinFamilySerializer, FamilySerializer, UserSerializer, UserCreatSerializer, WaitUserSerializer
from albums.models import FaceImage
from albums.serializers import EditFaceSerializer
import requests, json
import jwt
from decouple import config
import datetime
from .forms import DeviceForm

# Create your views here.
@api_view(['GET', 'POST', 'DELETE'])
@permission_classes([IsAuthenticated])
@authentication_classes((JSONWebTokenAuthentication,))
def JoinFamily(request, user_pk):
    if request.method == 'GET':
        wait_users = WaitUser.objects.filter(main_member=user_pk)
        serializers = WaitUserSerializer(wait_users,many=True)
        return Response({"data": serializers.data})
    elif request.method == 'POST':
        User = get_user_model()
        user = get_object_or_404(User, username=request.data.get('username'))
        main_member = get_object_or_404(User, pk=user_pk)
        serializer = UserSerializer(data={'username': user.username, 'state': 2, 'family': main_member.family_id}, instance=user)
        if serializer.is_valid(raise_exception=True):
            serializer.save()
            wait_user = get_object_or_404(WaitUser, wait_user=user.username)
            wait_user.delete()
        return Response(serializer.data)
    elif request.method == 'DELETE':
        wait_user = get_object_or_404(WaitUser, pk=user_pk)
        user = get_object_or_404(get_user_model(), username=wait_user.wait_user)
        serializer = UserSerializer(data={'username': user.username, 'state': 0}, instance=user)
        serializer.is_valid()
        print(serializer.errors)
        if serializer.is_valid(raise_exception=True):
            serializer.save()
            wait_user.delete()
            return Response({'message': '요청이 취소되었습니다.'})
    return Response(status=status.HTTP_400_BAD_REQUEST)
    

@api_view(['GET', 'POST', 'DELETE'])
@permission_classes([IsAuthenticated])
@authentication_classes((JSONWebTokenAuthentication,))
def MakeFamily(request):
    user = get_object_or_404(get_user_model(), username=request.user)
    if request.method == 'GET':
        data = request.GET.get('req')
        if request.user.state == 1:
            return Response(status=403, data={'message': '요청이 있습니다.'})
        elif data.isdigit():
            family = get_object_or_404(Family, pk=data)
            serializer = JoinFamilySerializer(data={'main_member': family.main_member,'wait_user': user.username})
        else:
            main_user = get_object_or_404(get_user_model(), username=data)
            if main_user.state == 3:
                serializer = JoinFamilySerializer(data={'main_member': main_user.pk,'wait_user': user.username})
            else:
                return Response(status=403, data={'message': '메인 멤버가 아닙니다'})
        if serializer.is_valid(raise_exception=True):
            serializer.save()
            userSerializers = UserSerializer(data={'username': user.username, 'state': 1}, instance=user)
            if userSerializers.is_valid(raise_exception=True):
                userSerializers.save()
        return Response(serializer.data)
    elif request.method == 'POST' and not user.family_id:
        if user.state == 1:
            return Response(status=403, data={'message': '요청이 있습니다.'})
        familySerializer = FamilySerializer(data={'main_member': user.id})
        if familySerializer.is_valid(raise_exception=True):
            familySerializer.save()
            userSerializer = UserSerializer(data={'username': user.username, 'state': 3, 'family': familySerializer.data['id']}, instance=user)
            if userSerializer.is_valid(raise_exception=True):
                userSerializer.save()
                return Response(familySerializer.data)
    elif request.method == 'DELETE':
        wait_user = get_object_or_404(WaitUser, wait_user=user)
        wait_user.delete()
        serializer = UserSerializer(data={'username': user.username, 'state': 0}, instance=user)
        if serializer.is_valid(raise_exception=True):
            serializer.save()
            return Response({'message': '요청이 취소되었습니다.'})
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


@api_view(['GET', 'POST'])
@permission_classes([AllowAny])
@csrf_exempt
def signup(request):
    if request.method == 'GET':
        data = request.GET.get('username')
        user = User.objects.filter(username=data)        
        if user.count() == 0:
            token = "true"
        else:
            token = "false"
        context = {"token":token}
        return JsonResponse(context)        
    elif request.method == 'POST':
        serializer = UserCreatSerializer(data=request.data)
        serializer.is_valid()
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
def checkDevice(request):
    User = get_user_model()
    try:
        user_id = int(request.POST.get('user_id')) + 1
    except:
        return Response(status=status.HTTP_401_UNAUTHORIZED)
    user = User.objects.get(id=user_id)

    device_token = request.POST.get('token')

    data = {
        'device_token': device_token,
        'owner': user
    }
    
    user.last_login = datetime.datetime.now(datetime.timezone.utc)
    user.save()

    target = Device.objects.filter(device_token=device_token)
    if len(target) > 0:
        return Response('Re-Hi!', status=status.HTTP_208_ALREADY_REPORTED)
    else:
        form = DeviceForm(data)
        if form.is_valid():
            device = form.save()
            device.save()
            return Response('Welcome!', status=status.HTTP_201_CREATED)
        else:
            return Response('what!', status=status.HTTP_401_UNAUTHORIZED)


@api_view(['GET'])
@permission_classes([AllowAny])
def getTarget(request):
    User = get_user_model()
    target = User.objects.filter(last_login__lt=datetime.date(2020, 5, 20))
    print(target)
    return Response(status=status.HTTP_200_OK)