from django.views.decorators.csrf import csrf_exempt
from .models import User, Family, WaitUser, Device, Mission, Score, Quiz
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
from .serializers import JoinFamilySerializer, FamilySerializer, UserSerializer, UserCreatSerializer, WaitUserSerializer, DetailFamilySerializer, MissionSerializer, ScoreSerializer, QuizSerializer, UserChangeSerializer
import requests, json
import jwt
from decouple import config
from .forms import DeviceForm
from korean_lunar_calendar import KoreanLunarCalendar
import datetime
from .helper import email_auth_num
import random


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
        familyName = get_object_or_404(FamilyName, user=user_pk, album=request.data.get('albumId'))
        if familyName.call in ["엄마", "아빠"]:
            state = 4
        else:
            state = 2
        serializer = UserSerializer(data={'username': user.username, 'state': state, 'family': main_member.family_id}, instance=user)
        if serializer.is_valid(raise_exception=True):
            serializer.save()
            if makeFamilyName(main_member.family_id, user_pk, serializer.data['id'], request.data.get('albumId'), familyName):
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
    '남동생엄마' : ['엄마'], '여동생엄마': ['엄마'], '남동생아빠': ['아빠'], '여동생아빠': ['아빠'],
    '형엄마': ['엄마'], '누나엄마': ['엄마'], '형아빠': ['아빠'], '누나아빠': ['아빠'],
    '오빠엄마': ['엄마'], '오빠아빠': ['아빠'], '언니엄마': ['엄마'], '언니아빠': ['아빠'],
    '엄마남동생': ['아들'], '엄마여동생': ['딸'], '아빠남동생': ['아들'], '아빠여동생': ['딸'],
    '엄마형': ['아들'], '엄마누나': ['딸'], '아빠형': ['아들'], '아빠누나': ['딸'],
    '엄마오빠': ['아들'], '아빠오빠': ['아들'], '엄마언니': ['딸'], '아빠언니': ['딸'],
    '아빠엄마': ['아내'], '엄마아빠': ['남편'],
    '남동생누나': ['누나'], '남동생형': ['형'], '여동생누나': ['언니'], '여동생형': ['오빠'],
    '남동생언니': ['누나'], '남동생오빠': ['형'], '여동생언니': ['언니'], '여동생오빠': ['오빠'],
    '누나남동생': ['남동생'], '형남동생': ['남동생'], '누나여동생': ['여동생'], '형여동생': ['여동생'],
    '언니남동생': ['남동생'], '오빠남동생': ['남동생'], '언니여동생': ['여동생'], '오빠여동생': ['여동생'],

    '언니': ['여동생'], '오빠': ['여동생'], '형': ['남동생'], '누나': ['남동생'],
    '엄마': ['아들', '딸'], '아빠': ['아들', '딸'],'여동생': ['오빠', '언니'], '남동생': ['형', '누나'],

    '언니언니': ['언니', '여동생'], '누나누나': ['언니', '여동생'], '형형': ['형', '남동생'], '오빠오빠': ['형', '남동생'],
    '남동생남동생': ['형', '남동생'], '여동생여동생': ['언니', '여동생'],
    '누나형': ['남동생', '누나'], '형누나': ['여동생', '오빠'],
    '언니오빠': ['남동생', '누나'], '오빠언니': ['여동생', '오빠']
}
# user_pk : 담장 pk, owner : 새로운 가족원 "앨범 주인" pk, albumId : 새로운 가족원 지정 앨범
def makeFamilyName(family_pk, user_pk, owner, albumId, familyName):
    # 담장과 지정 앨범 불러와 앨범 주인 저장
    familyNameSerializer = FamilyNameupdateSerializer(data={'user': user_pk, 'owner': owner, 'album': albumId}, instance=familyName)
    ownerInfo = get_object_or_404(get_user_model(), pk=owner)
    if familyNameSerializer.is_valid():
        familyNameSerializer.save()
        # 모든 가족 구성원과 지정 앨범을 불러와 앨범 주인 지정
        # user 가 owner와 album을 call 이라고 부름
        users = get_user_model().objects.filter(family=family_pk)
        for user in users:
            # 가족원과 앨범 주인이 동일 인물일 경우 call = 나
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
                else:
                    return True
            # 가족원이 담장일 경우 새로운 가족원이 담장을 부르는 경우만 새로 생성
            # 담장은 맨 위에서 담장이 선택한 call로 이미 지정함.
            elif user.id == user_pk:
                member = get_object_or_404(FamilyName, user=user_pk, owner=user.id)
                # 관계로만 호칭 선정이 어려운 경우 "ex. 나와 엄마 -> 내가 아들인지 딸인지 판별, 나와 여동생 -> 내가 언니인지 오빠인지"
                if user.gender == 2 and (familyName.call=='엄마' or familyName.call=='아빠' or familyName.call=='여동생' or familyName.call=='남동생'):
                    try:
                        familyNameSerializer3 = FamilyNameSerializer(data={'user': owner, 'owner': user_pk, 'album': member.album.id, 'call': namedic[familyName.call][1]})
                    except KeyError:
                        familyNameSerializer3 = FamilyNameSerializer(data={'user': owner, 'owner': user_pk, 'album': member.album.id, 'call': user.first_name})
                # 관계만으로 호칭 선정 가능한 경우 "ex. 나와 오빠 -> 여동생"
                else:
                    try:
                        familyNameSerializer3 = FamilyNameSerializer(data={'user': owner, 'owner': user_pk, 'album': member.album.id, 'call': namedic[familyName.call][0]})
                    except KeyError:
                        familyNameSerializer3 = FamilyNameSerializer(data={'user': owner, 'owner': user_pk, 'album': member.album.id, 'call': user.first_name})
                if familyNameSerializer3.is_valid():
                    familyNameSerializer3.save()
                else:
                    return True
            # 가족원이 담장이 아닐경우 기존 가족원이 새로운 가족원을 부르는 이름이 정당한지 판단
            # "ex. 기존에는 형으로 되어있었으나 새로운 가족원과 생년 비교 후 반대인 경우 변경"
            # 기존 call이 정당한 경우 앨범 주인만 지정
            # 관계가 모호한 경우 생년월일로 비교 후 지정
            # "ex. 형형 -> 둘중 누가 형인지 판단 후 지정"
            else:
                # 담장이 기존 가족원을 부르는 호칭
                member = get_object_or_404(FamilyName, user=user_pk, owner=user.id)

                # 기존 가족원이 새로운 가족원을 부르는 호칭 "기존 앨범 호칭"
                album = get_object_or_404(FamilyName, user=user.id, album=albumId)

                # 담장이 기존 가족원과 새로운 가족원을 부르는 호칭이 같은 경우 "형형, 누나누나"
                # 기존 가족원과 새로운 가족원의 생년을 비교하여 호칭 변경
                if familyName.call==member.call:
                    if ownerInfo.birth < user.birth:
                        familyNameSerializer2 = FamilyNameupdateSerializer(data={'user': user.id, 'owner': owner, 'album': albumId}, instance=album)
                        try:
                            familyNameSerializer3 = FamilyNameSerializer(data={'user': owner, 'owner': user.id, 'album': member.album.id, 'call': namedic[familyName.call+member.call][1]})
                        except KeyError:
                            familyNameSerializer3 = FamilyNameSerializer(data={'user': owner, 'owner': user.id, 'album': member.album.id, 'call': user.first_name})
                    else:
                        try:
                            familyNameSerializer2 = FamilyNameupdateSerializer(data={'user': user.id, 'owner': owner, 'album': albumId, 'call': namedic[familyName.call+member.call][1]}, instance=album)
                        except KeyError:
                            familyNameSerializer2 = FamilyNameupdateSerializer(data={'user': user.id, 'owner': owner, 'album': albumId, 'call': ownerInfo.first_name}, instance=album)
                        try:
                            familyNameSerializer3 = FamilyNameSerializer(data={'user': owner, 'owner': user.id, 'album': member.album.id, 'call': namedic[familyName.call+member.call][0]})
                        except KeyError:
                            familyNameSerializer3 = FamilyNameSerializer(data={'user': owner, 'owner': user.id, 'album': member.album.id, 'call': user.first_name})

                elif familyName.call+member.call in ['누나형', '형누나', '언니오빠', '오빠언니']:
                    if ownerInfo.birth < user.birth:
                        try:
                            familyNameSerializer2 = FamilyNameupdateSerializer(data={'user': user.id, 'owner': owner, 'album': albumId, 'call': namedic[familyName.call+member.call][1]}, instance=album)
                        except KeyError:
                            familyNameSerializer2 = FamilyNameSerializer(data={'user': owner, 'owner': user.id, 'album': member.album.id, 'call': user.first_name})
                        try:
                            familyNameSerializer3 = FamilyNameSerializer(data={'user': owner, 'owner': user.id, 'album': member.album.id, 'call': namedic[familyName.call+member.call][0]})
                        except KeyError:
                            familyNameSerializer3 = FamilyNameSerializer(data={'user': owner, 'owner': user.id, 'album': member.album.id, 'call': user.first_name})
                    else:
                        try:
                            familyNameSerializer2 = FamilyNameupdateSerializer(data={'user': user.id, 'owner': owner, 'album': albumId, 'call': namedic[member.call+familyName.call][0]}, instance=album)
                        except KeyError:
                            familyNameSerializer2 = FamilyNameSerializer(data={'user': owner, 'owner': user.id, 'album': member.album.id, 'call': user.first_name})
                        try:
                            familyNameSerializer3 = FamilyNameSerializer(data={'user': owner, 'owner': user.id, 'album': member.album.id, 'call': namedic[member.call+familyName.call][1]})
                        except KeyError:
                            familyNameSerializer3 = FamilyNameSerializer(data={'user': owner, 'owner': user.id, 'album': member.album.id, 'call': user.first_name})
                else:
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

        nullOwnerAlbum = Album.objects.filter(member=None).exclude(title="기본 앨범")
        for noa in nullOwnerAlbum:
            member = get_object_or_404(FamilyName, user=user_pk, album=noa.id)
            try:
                familyNameSerializer2 = FamilyNameSerializer(data={'user': owner, 'album': noa.id, 'call': namedic[familyName.call+member.call][0]})
            except KeyError:
                familyNameSerializer2 = FamilyNameSerializer(data={'user': owner, 'album': noa.id, 'call': member.call})
            if familyNameSerializer2.is_valid():
                familyNameSerializer2.save()

        
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
        if not Score.objects.filter(user=user):
            Score.objects.create(user=user)
        serializer = UserSerializer(user)
        data = serializer.data
        album = FamilyName.objects.filter(user=data['id'], owner=data['id'])
        if len(album) > 0:
            data['my_album'] = True
        else:
            data['my_album'] = False
        return Response(serializer.data)
    elif request.method == 'PUT':
        User = get_user_model()
        user = get_object_or_404(User, username=request.user)
        serializer = UserSerializer(data=request.data, instance=user)
        if serializer.is_valid(raise_exception=True):
            serializer.save()
            return Response(serializer.data)
    return Response(status=status.HTTP_400_BAD_REQUEST)




@api_view(['GET', 'POST','PUT'])
@permission_classes([AllowAny])
@csrf_exempt
def signup(request):
    print('aaa')
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

@api_view(['GET', 'POST'])
@permission_classes([AllowAny])
@csrf_exempt
def FindPassword(request):
    if request.method == 'GET':
        username = request.GET.get('username')
        user = get_object_or_404(get_user_model(), username=username)
        token = "true"
        auth_num = email_auth_num()
        serializer = UserChangeSerializer(data={'username': username, 'password': auth_num}, instance=user)
        serializer.is_valid()
        if serializer.is_valid(raise_exception=True):
            user = serializer.save()
            print(user)
        else:
            token = "false"
        context = {"token":token}
        return JsonResponse(context)        
    elif request.method == 'POST':
        user = get_object_or_404(User, username=request.data.get("username"))
        serializer = UserChangeSerializer(data={'username': user.username, 'password': request.data.get("password")}, instance=user)
        print(serializer)
        serializer.is_valid()
        if serializer.is_valid(raise_exception=True):
            user = serializer.save()

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

@api_view(['GET', 'PUT'])
def missions(request, user_pk, period):
    if request.method == 'GET':
        missions = Mission.objects.filter(user=user_pk, period=period)
        today = datetime.date.today()
        user = get_object_or_404(User, id=user_pk)
        if period == 0:
            if len(missions) != 0:
                if missions[0].created_at.day != today.day:
                    missions.delete()
                    for i in range(5):
                        Mission.objects.create(user=user, title=f"엄마랑 사진 {i+1}장 찍기", status=0, point= 100*(i+1), prize=0, period=period)
            else:
                for i in range(5):
                    Mission.objects.create(user=user, title=f"엄마랑 사진 {i+1}장 찍기", status=0, point= 100*(i+1), prize=0, period=period)
        elif period == 1:
            if len(missions) != 0:
                if today.weekday() == 1 and missions[0].created_at.day != today.day:
                        missions.delete()
                        for i in range(5, 10, 1):
                            Mission.objects.create(user=user, title=f"엄마랑 사진 {i+1}장 찍기", status=0, point= 100*(i+1), prize=0, period=period)
            else:
                for i in range(5, 10, 1):
                    Mission.objects.create(user=user, title=f"엄마랑 사진 {i+1}장 찍기", status=0, point= 100*(i+1), prize=0, period=period)
        elif period == 2:
            if len(missions) != 0:
                if today.day == 0 and missions[0].created_at.month != today.month:
                    missions.delete()
                    for i in range(100, 105, 1):
                        Mission.objects.create(user=user, title=f"엄마랑 사진 {i+1}장 찍기", status=0, point= 100*(i+1), prize=0, period=period)
            else:
                for i in range(100, 105, 1):
                    Mission.objects.create(user=user, title=f"엄마랑 사진 {i+1}장 찍기", status=0, point= 100*(i+1), prize=0, period=period)
        missions = Mission.objects.filter(user=user_pk, period=period)
        serializers = MissionSerializer(missions, many=True)
        return Response({"data": serializers.data})
    if request.method == 'PUT':
        mission_title = json.loads(request.data.get('mission_title'))
        mission_id = json.loads(request.data.get('mission_id'))
        mission = Mission.objects.filter(id=mission_id, user=user_pk, title=mission_title, period=period)[0]
        mission.status = 1
        mission.save()
        serializer = MissionSerializer(mission)
        return Response(serializer.data)


@api_view(['GET', 'PUT', ])
def score(request, user_pk):
    if request.method == 'GET':
        user = get_object_or_404(User, id=user_pk)
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


@api_view(['POST'])
@permission_classes([IsAuthenticated])
@authentication_classes((JSONWebTokenAuthentication,))
@csrf_exempt
def logout(request):
    print('-----------------------------------------------------------')
    print(request.data)
    device_token = request.POST.get('device_token')
    print(device_token)
    target = get_object_or_404(Device, device_token=device_token)
    target.delete()
    return Response(status=status.HTTP_200_OK)

        
@api_view(['GET', 'PUT'])
def makequiz(request, family_pk, user_pk):
    if request.method == 'GET':
        user = get_object_or_404(User, id=user_pk)
        quizs = Quiz.objects.filter(user=user_pk)
        today = datetime.date.today()
        with open(f'quiz/quiz.json', 'r', encoding='utf-8') as quiz:
            data = json.load(quiz)
        quiz_question = data['parent']
        todays_quiz = random.sample(range(0, len(quiz_question)), 2)
        if len(quizs) != 0:
            if quizs[0].created_at.day != today.day:
                quizs.delete()
                for i in range(2):
                    Quiz.objects.create(user=user, quiz=quiz_question[todays_quiz[i]])
        else:
            for i in range(2):
                Quiz.objects.create(user=user, quiz=quiz_question[todays_quiz[i]])
        quizs = Quiz.objects.filter(user=user_pk)
        serializers = QuizSerializer(quizs, many=True)
        return Response({'data': serializers.data, 'name': user.first_name})
    elif request.method == 'PUT':
        parent_user = get_object_or_404(User, id=user_pk)
        users = User.objects.filter(family=family_pk).exclude(state=4)
        quiz_id = request.data['id']
        quiz = Quiz.objects.filter(id=quiz_id, user=user_pk)[0]
        answer = json.loads(request.data['answer'])
        quiz.answer = answer
        quiz.save()
        for user in users:
            Quiz.objects.create(user=user, quiz=f'{parent_user.first_name}님의 {quiz.quiz}', answer=answer)
        return Response(1, status=status.HTTP_200_OK)


@api_view(['GET', 'POST', ])
def getquiz(request, user_pk):
    if request.method == 'GET':
        quizs = Quiz.objects.filter(user=user_pk)
        if len(quizs) == 0:
            return Response(0, status=status.HTTP_400_BAD_REQUEST)
        else:
            serializer = QuizSerializer(quizs[0])
            return Response(serializer.data)
    elif request.method == 'POST':
        quiz_id = json.loads(request.data.get('quiz_id'))
        score = Score.objects.filter(user=user_pk)[0]
        score.score += 5
        score.save()
        quiz = Quiz.objects.filter(user=user_pk, id=quiz_id)[0]
        quiz.delete()
        return Response(1, status=status.HTTP_200_OK)