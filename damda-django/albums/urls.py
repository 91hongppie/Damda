from django.urls import path, include
from . import views
from rest_framework_swagger.views import get_swagger_view


app_name = "albums"
urlpatterns = [
    path('photo/<int:album_pk>/', views.photo),
    path('<int:family_pk>/', views.album),
]
