from django.views.decorators.csrf import csrf_exempt
from allauth.socialaccount.providers.kakao.views import KakaoOAuth2Adapter
from rest_auth.registration.views import SocialLoginView
from django.utils.decorators import method_decorator

# Create your views here.


# @csrf_exempt
# def app_login(request):
#     pass

@method_decorator(csrf_exempt, name='dispatch')
class KakaoLogin(SocialLoginView):
    adapter_class = KakaoOAuth2Adapter