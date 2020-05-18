from django.shortcuts import render
from django.views.decorators.csrf import csrf_exempt
from .models import User
from rest_framework.response import Response
from django.http import JsonResponse


# Create your views here.


@csrf_exempt
def checkemail(request):
    print(request)
    if request.method == 'POST':
        users = User.objects.all()
        print(users)
    
        if id in users: 
            return JsonResponse({'token':'exist'})
        else:
            return JsonResponse({'token': ''})