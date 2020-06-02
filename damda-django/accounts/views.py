from django.views.decorators.csrf import csrf_exempt
from .models import User, Family, WaitUser, Device, Mission, Score
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
from albums.models import Album, FamilyName
from albums.serializers import EditFaceSerializer, AlbumSerializer, FamilyNameSerializer, FamilyNameupdateSerializer
from .serializers import JoinFamilySerializer, FamilySerializer, UserSerializer, UserCreatSerializer, WaitUserSerializer, DetailFamilySerializer, MissionSerializer, ScoreSerializer
import requests, json
import jwt
from decouple import config
from .forms import DeviceForm
from korean_lunar_calendar import KoreanLunarCalendar
import datetime


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
            if makeFamilyName(main_member.family_id, user_pk, serializer.data['id'], request.data.get('albumId')):
                tmp_user = get_object_or_404(User, pk=serializer.data['id'])
                tmp_user.delete()
                return Response(status=status.HTTP_400_BAD_REQUEST)
            wait_user = get_object_or_404(WaitUser, wait_user=user.username)
            wait_user.delete()
        return Response(serializer.data)
    elif request.method == 'DELETE':
        wait_user = get_object_or_404(WaitUser, pk=user_pk)
        user = get_object_or_404(get_user_model(), username=wait_user.wait_user)
        serializer = UserSerializer(data={'username': user.username, 'state': 0}, instance=user)
        serializer.is_valid()
        if serializer.is_valid(raise_exception=True):
            serializer.save()
            wait_user.delete()
            return Response({'message': '요청이 취소되었습니다.'})
    return Response(status=status.HTTP_400_BAD_REQUEST)


namedic={
    '남동생엄마' : ['엄마', '아들'], '여동생엄마': ['엄마', '딸'], '남동생아빠': ['아빠', '아들'], '여동생아빠': ['아빠', '딸'],
    '형엄마': ['엄마', '아들'], '누나엄마': ['엄마', '딸'], '형아빠': ['아빠', '아들'], '누나아빠': ['아빠', '딸'],
    '오빠엄마': ['엄마', '아들'], '오빠아빠': ['아빠','아들'], '언니엄마': ['엄마', '딸'], '언니아빠': ['아빠','딸'],
    '엄마남동생': ['아들', '엄마'], '엄마여동생': [ '딸', '엄마'], '아빠남동생': ['아들', '아빠'], '아빠여동생': ['딸','아빠'],
    '엄마형': ['아들', '엄마'], '엄마누나': [ '딸','엄마'], '아빠형': ['아들','아빠'], '아빠누나': ['딸','아빠'],
    '엄마오빠': ['아들', '엄마'], '아빠오빠': ['아들', '아빠'], '엄마언니': ['딸', '엄마'], '아빠언니': ['딸', '아빠'],
    '아빠엄마': ['아내', '남편'], '엄마아빠': ['남편', '아내'],
    '남동생누나': ['누나', '남동생'], '남동생형': ['형', '남동생'], '여동생누나': ['언니', '여동생'], '여동생형': ['오빠', '여동생'],
    '남동생언니': ['누나', '남동생'], '남동생오빠': ['형', '남동생'], '여동생언니': ['언니', '여동생'], '여동생오빠': ['오빠', '여동생'],
    '누나남동생': ['남동생', '누나'], '형남동생': ['남동생', '형'], '누나여동생': ['여동생', '언니'], '형여동생': ['여동생', '오빠'],
    '언니남동생': ['남동생', '누나'], '오빠남동생': ['남동생', '형'], '언니여동생': ['여동생','언니'], '오빠여동생': ['여동생', '오빠'],
    '언니언니': ['언니', '여동생'], '누나누나': ['언니', '여동생'], '형형': ['형', '남동생'], '오빠오빠': ['형', '남동생'],
    '남동생남동생': ['형', '남동생'], '여동생여동생': ['언니', '여동생'], '누나형': ['남동생', '누나'], '형누나': ['여동생', '오빠'],
    '언니오빠': ['남동생', '누나'], '오빠언니': ['여동생', '오빠'],
    '나엄마': ['아들', '딸'], '나아빠': ['아들', '딸'], '나언니': ['여동생'], '나오빠': ['여동생'], '나형': ['남동생'], '나누나': ['남동생'],
    '나여동생': ['오빠', '언니'], '나남동생': ['형', '누나']
}
def makeFamilyName(family_pk, user_pk, owner, albumId):
    familyName = get_object_or_404(FamilyName, user=user_pk, album=albumId)
    familyNameSerializer = FamilyNameupdateSerializer(data={'user': user_pk, 'owner': owner, 'album': albumId}, instance=familyName)
    if familyNameSerializer.is_valid():
        familyNameSerializer.save()
        
        users = get_user_model().objects.filter(family=family_pk)
        for user in users:
            if user.id == owner:
                familyNameSerializer2 = FamilyNameSerializer(data={'user': user.id, 'owner': owner, 'album': albumId, 'call': '나'})
                if familyNameSerializer2.is_valid():
                    familyNameSerializer2.save()
                    face = get_object_or_404(Album, pk=albumId)
                    serializer = EditFaceSerializer(data={'member': owner}, instance=face)
                    if serializer.is_valid():
                        serializer.save()
                else:
                    return True
            elif user.id == user_pk:
                member = get_object_or_404(FamilyName, user=user_pk, owner=user.id)
                try:
                    familyNameSerializer3 = FamilyNameSerializer(data={'user': owner, 'owner': user.id, 'album': member.album.id, 'call': namedic[familyName.call+member.call][0]})
                except KeyError:
                    familyNameSerializer3 = FamilyNameSerializer(data={'user': owner, 'owner': user.id, 'album': member.album.id, 'call': user.first_name})
                if familyNameSerializer3.is_valid():
                    familyNameSerializer3.save()
                else:
                    return True
            else:
                member = get_object_or_404(FamilyName, user=user_pk, owner=user.id)
                album = get_object_or_404(FamilyName, user=user.id, album=albumId)
                try:
                    familyNameSerializer2 = FamilyNameupdateSerializer(data={'user': user.id, 'owner': owner, 'album': albumId}, instance=album)
                except KeyError:
                    ownerData = get_object_or_404(get_user_model(), pk=owner)
                    familyNameSerializer2 = FamilyNameupdateSerializer(data={'user': user.id, 'owner': owner, 'album': albumId}, instance=album)
                try:
                    familyNameSerializer3 = FamilyNameSerializer(data={'user': owner, 'owner': user.id, 'album': member.album.id, 'call': namedic[familyName.call+member.call][0]})
                except KeyError:
                    familyNameSerializer3 = FamilyNameSerializer(data={'user': owner, 'owner': user.id, 'album': member.album.id, 'call': user.first_name})

                if familyNameSerializer2.is_valid():
                    familyNameSerializer2.save()
                else:
                    return True
                if familyNameSerializer3.is_valid():
                    familyNameSerializer3.save()
                else:
                    return True
        
    else:
        return True
    

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
            albumSerializer = AlbumSerializer(data={'family':familySerializer.data['id'], 'title':'기본 앨범', 'image': "empty"})
            if userSerializer.is_valid(raise_exception=True) and albumSerializer.is_valid(raise_exception=True):
                userSerializer.save()
                albumSerializer.save()
                return Response(familySerializer.data)
    elif request.method == 'DELETE':
        wait_user = get_object_or_404(WaitUser, wait_user=user)
        wait_user.delete()
        serializer = UserSerializer(data={'username': user.username, 'state': 0}, instance=user)
        if serializer.is_valid(raise_exception=True):
            serializer.save()
            return Response({'message': '요청이 취소되었습니다.'})
    return Response(status=status.HTTP_400_BAD_REQUEST)

@api_view(['GET'])
@permission_classes([IsAuthenticated])
@authentication_classes((JSONWebTokenAuthentication,))
def DetailFamily(request, family_pk):
    family = get_object_or_404(Family, pk=family_pk)
    serializer = DetailFamilySerializer(family)
    members = []
    calendar = KoreanLunarCalendar()
    year = datetime.date.today().year
    for member in serializer.data['members']:
        birth = member['birth']
        if birth and member['is_lunar']:
            Y, month, day = birth.split('-')
            calendar.setLunarDate(year, int(month), int(day), False)
            birth = calendar.SolarIsoFormat()
        members.append({'username': member['username'], 'first_name': member['first_name'], 'birth': birth})
    return Response({'main_member': get_object_or_404(get_user_model(), pk=serializer.data['main_member']).username,
                    'members': members})

@api_view(['GET', 'POST'])
@permission_classes([IsAuthenticated])
@authentication_classes((JSONWebTokenAuthentication,))
def GetFamily(request, family_pk):
    if request.method == 'GET':
        users = User.objects.filter(family=family_pk)
        serializer = UserSerializer(users, many=True)
        return Response({'data': serializer.data})
    elif request.method == 'POST':
        face = get_object_or_404(Album, pk=request.data.get('face_id'))
        member_id = request.data.get('member_id')
        if member_id == 0:
            member_id = None
        serializer = EditFaceSerializer(data={'member': member_id}, instance=face)
        if serializer.is_valid():
            serializer.save()
            return Response(serializer.data)
    return Response(status=status.HTTP_400_BAD_REQUEST)

@api_view(['GET', 'PUT'])
@permission_classes([IsAuthenticated])
@authentication_classes((JSONWebTokenAuthentication,))
def UserInfo(request):
    if request.method == 'GET':
        User = get_user_model()
        user = get_object_or_404(User, username=request.user)
        serializer = UserSerializer(user)
        data = serializer.data
        # album = FamilyName.objects.filter(user=data.id, owner=data.id)
        # if len(album) > 0:
        #     data['my_album'] = True
        # else:
        #     data['my_album'] = False
        return Response(serializer.data)
    elif request.method == 'PUT':
        User = get_user_model()
        user = get_object_or_404(User, username=request.user)
        serializer = UserSerializer(data=request.data, instance=user)
        if serializer.is_valid(raise_exception=True):
            serializer.save()
            return Response(serializer.data)
    return Response(status=status.HTTP_400_BAD_REQUEST)




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
            user = serializer.save()
            Score.objects.create(user=user)
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
        user_id = int(request.POST.get('user_id'))
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

@api_view(['GET', ])
def missions(request, user_pk, period):
    missions = Mission.objects.filter(user=user_pk, period=period)
    serializers = MissionSerializer(missions, many=True)
    return Response({"data": serializers.data})


@api_view(['GET', 'PUT', ])
def score(request, user_pk):
    if request.method == 'GET':
        user = User.objects.filter(id=user_pk)[0]
        score = Score.objects.filter(user=user_pk)[0]
        serializer = ScoreSerializer(score)
        data = {"name": user.first_name, "score": score.score}
    elif request.method == 'PUT':
        user = User.objects.filter(id=user_pk)[0]
        score = Score.objects.filter(user=user_pk)[0]
        mission = Mission.objects.filter(user=user_pk, id=request.data['mission_id'])[0]
        mission.prize = 1
        mission.save()
        score.score = request.data['score']
        score.save()
        data = {"name": user.first_name, "score": score.score}
    return Response(data)