from django.contrib import admin
from .models import User, Mission, Quiz

# Register your models here.
admin.site.register(User)
admin.site.register(Mission)
admin.site.register(Quiz)