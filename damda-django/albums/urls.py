from django.urls import path, include
from . import views
from rest_framework_swagger.views import get_swagger_view


app_name = "albums"
urlpatterns = [
    path('photo/<int:family_pk>/<int:album_pk>/', views.photo),
    path('photo/<int:family_pk>/', views.all_photo),
    path('<int:family_pk>/', views.albums),
    path('album/<int:album_pk>/', views.album),
    path('<int:family_pk>/face/', views.face),
    path('addphoto/', views.addphoto)
]
