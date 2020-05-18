from django.shortcuts import render
from django.views.decorators.csrf import csrf_exempt
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
import jwt

# Create your views here.

@api_view(['GET'])
@permission_classes([IsAuthenticated])
@authentication_classes((JSONWebTokenAuthentication,))
def UserInfo(request):
    encoded_token = request.headers['Authorization'].split()[1]
    pk = jwt.decode(encoded_token, 'SECRET', algorithms=['HS256'])['user_id']
    User = get_user_model()
    user = get_object_or_404(User, pk=pk)
    serializer = UserSerializer(user)
    return Response(serializer.data)


@method_decorator(csrf_exempt, name='dispatch')
class KakaoLogin(SocialLoginView):
    adapter_class = KakaoOAuth2Adapter