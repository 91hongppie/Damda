from django.urls import path, include
from . import views
from rest_framework_swagger.views import get_swagger_view


app_name = "accounts"
urlpatterns = [
    path('checkemail/',views.checkemail),
    path('signup/', views.signup),
    path('rest-auth/kakao/', views.KakaoLogin.as_view())
]
