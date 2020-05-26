from django.urls import path, include
from . import views
from rest_framework_swagger.views import get_swagger_view


app_name = "albums"
urlpatterns = [
    path('photo/<int:album_pk>/', views.photo),
    path('<int:family_pk>/', views.album),
    path('photo/delete/', views.photo_delete),
    path('<int:family_pk>/face/', views.face),
    path('<int:family_pk>/video/', views.video),
    path('<int:family_pk>/video/<int:video_pk>/', views.detail_video),

]
