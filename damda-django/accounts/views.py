from django.views.decorators.csrf import csrf_exempt
from .models import User
from rest_framework.response import Response
from django.http import JsonResponse

from allauth.socialaccount.providers.kakao.views import KakaoOAuth2Adapter
from rest_auth.registration.views import SocialLoginView
from django.utils.decorators import method_decorator
from django.shortcuts import get_object_or_404
from django.contrib.auth import get_user_model
from rest_framework.permissions import IsAuthenticated
from rest_framework.decorators import api_view, permission_classes, authentication_classes
from rest_framework.response import Response
from .serializers import UserSerializer
from rest_framework_jwt.authentication import JSONWebTokenAuthentication

# Create your views here.

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

@csrf_exempt
def signup(request):
    if request.method == 'POST':
        user = User()
        user.username = request.POST.get('username',None)
        user.save()
    return JsonResponse({"token":"true"})

@method_decorator(csrf_exempt, name='dispatch')
class KakaoLogin(SocialLoginView):
    adapter_class = KakaoOAuth2Adapter
