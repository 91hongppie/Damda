from django.urls import path, include
from . import views
from rest_framework_swagger.views import get_swagger_view
from rest_framework_jwt.views import obtain_jwt_token


app_name = "accounts"
urlpatterns = [
    path('device/', views.checkDevice),
    path('signup/', views.signup),
    path('rest-auth/kakao/', views.KakaoLogin.as_view()),
    path('user/', views.UserInfo),
    path('user/<int:user_pk>/', views.JoinFamily),
    path('family/', views.MakeFamily),
    path('family/<int:family_pk>/', views.DetailFamily),
    path('family_info/<int:family_pk>/', views.GetFamily),
    path('mission/<int:user_pk>/<int:period>/', views.missions),
    path('score/<int:user_pk>/', views.score),
    path('logout/', views.logout),
    path('pushswitch/', views.pushSwitch)
]
