from django.shortcuts import render
from django.views.decorators.csrf import csrf_exempt
from allauth.socialaccount.providers.kakao.views import KakaoOAuth2Adapter
from rest_auth.registration.views import SocialLoginView

# Create your views here.


@csrf_exempt
def app_login(request):
    pass

class KakaoLogin(SocialLoginView):
    adapter_class = KakaoOAuth2Adapter