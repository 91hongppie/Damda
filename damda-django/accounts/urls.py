from django.urls import path, include
from . import views
from rest_framework_swagger.views import get_swagger_view


app_name = "accounts"
urlpatterns = [
    # path('app_login',views.app_login),
    path('rest-auth/kakao/', views.KakaoLogin)
]
