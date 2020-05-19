from django.urls import path, include
from . import views
from rest_framework_swagger.views import get_swagger_view
from rest_framework_jwt.views import obtain_jwt_token


app_name = "accounts"
urlpatterns = [
    path('msg/', views.messaging),
    path('checkemail/',views.checkemail),
    path('signup/', views.signup),
    path('rest-auth/kakao/', views.KakaoLogin.as_view()),
    path('user/', views.UserInfo),
    path('family/', views.Family),
    path('family/<int:user_pk>/', views.JoinFamily)
]
