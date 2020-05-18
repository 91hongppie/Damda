from django.urls import path, include
from . import views
from rest_framework_swagger.views import get_swagger_view
from rest_framework_jwt.views import obtain_jwt_token


app_name = "accounts"
urlpatterns = [
    path('api-jwt-auth/', obtain_jwt_token),
    # path('app_login',views.app_login),
    path('rest-auth/kakao/', views.KakaoLogin.as_view())
]
