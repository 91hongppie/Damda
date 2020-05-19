from django.views.decorators.csrf import csrf_exempt
from .models import User
from rest_framework.response import Response
from django.http import JsonResponse

from allauth.socialaccount.providers.kakao.views import KakaoOAuth2Adapter
from rest_auth.registration.views import SocialLoginView
from django.utils.decorators import method_decorator
from .messaging import send_to_token

# Create your views here.


@csrf_exempt
def checkemail(request):
    data = request.GET.get('username', None)
    print(data)
    user = None
    if request.method == 'GET':
        user = User.objects.filter(username=data)
        print(user.count())
        
        if user.count() == 0:
            token = "true"
        else:
            token = "false"
        context = {"token":token}
        return JsonResponse(context)
@csrf_exempt
def signup(request):
    print(request.POST.get('username',None))
    if request.method == 'POST':
        user = User()
        user.username = request.POST.get('username',None)
        user.save()
    return JsonResponse({"token":"true"})

@method_decorator(csrf_exempt, name='dispatch')
class KakaoLogin(SocialLoginView):
    adapter_class = KakaoOAuth2Adapter


def messaging(request):
    send_to_token()
