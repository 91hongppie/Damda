from django.views.decorators.csrf import csrf_exempt
from .models import User
from rest_framework.response import Response
from django.http import JsonResponse
from allauth.socialaccount.providers.kakao.views import KakaoOAuth2Adapter
from rest_auth.registration.views import SocialLoginView
from django.utils.decorators import method_decorator
from .messaging import send_to_token
from django.shortcuts import get_object_or_404
from django.contrib.auth import get_user_model
from rest_framework.permissions import IsAuthenticated, AllowAny
from rest_framework.decorators import api_view, permission_classes, authentication_classes
from rest_framework.response import Response
from rest_framework_jwt.authentication import JSONWebTokenAuthentication
from .serializers import JoinFamilySerializer, FamilySerializer, UserSerializer

# Create your views here.
@api_view(['GET', 'POST'])
@permission_classes([IsAuthenticated])
@authentication_classes((JSONWebTokenAuthentication,))
def JoinFamily(request, user_pk):
    if request.method == 'GET':
        data = request.GET.get('req')
        if data.isdigit():
            family = get_object_or_404(Family, pk=data)
            serializer = JoinFamilySerializer(data={'main_member_id': family.main_user,'wait_user': request.user})
        else:
            user = get_object_or_404(get_user_model, username=data)
            if user.state == 3:
                serializer = JoinFamilySerializer(data={'main_member_id': data,'wait_user': request.user})
            else:
                return Response({'error': '메인 멤버가 아닙니다'})
        if serializer.is_valid(raise_exception=True):
            serializer.save()
            user = get_object_or_404(get_user_model, pk=user_pk)
            userSerializers = UserSerializer(data={'state': 1}, instance=user)
            if userSerializers.is_valid(raise_exception=True):
                userSerializers.save()
        return Response(serializer.data)
    elif request.method == 'POST': 
        User = get_user_model()
        user = get_object_or_404(User, pk=request.POST.get('id'))
        main_member = get_object_or_404(User, pk=user_pk)
        serializer = UserSerializer(data={'state': 2, 'family_id': main_member.family_id}, instance=user)
        if serializer.is_valid(raise_exception=True):
            serializer.save()
            wait_user = get_object_or_404(User, wait_user=user.username)
            wait_user.delete()
        return Response(serializer.data)
    return Response(status=status.HTTP_400_BAD_REQUEST)

@api_view(['POST'])
@permission_classes([IsAuthenticated])
@authentication_classes((JSONWebTokenAuthentication,))
def Family(request):
    User = get_user_model()
    user = get_object_or_404(User, username=request.user)
    familySerializer = FamilySerializer(data={'main_member': user.username})
    if familySerializer.is_valid(raise_exception=True):
        familySerializer.save()
        userSerializer = UserSerializer(data={'username': user.username, 'state': 3, 'family': familySerializer.data['id']}, instance=user)
        if userSerializer.is_valid(raise_exception=True):
            userSerializer.save()
            return Response(userSerializer.data)
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
    user = None
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
        serializer = UserSerializer(data=request.data)
        if serializer.is_valid(raise_exception=True):
            serializer.save()
            return Response(serializer.data)
        else:
            return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

@method_decorator(csrf_exempt, name='dispatch')
class KakaoLogin(SocialLoginView):
    adapter_class = KakaoOAuth2Adapter


def messaging(request):
    send_to_token()
