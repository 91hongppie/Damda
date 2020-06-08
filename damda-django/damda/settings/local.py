from .base import *
from decouple import config

# SECURITY WARNING: keep the secret key used in production secret!
SECRET_KEY = config('SECRET_KEY', 'uc*xu4vdpfu$sf)b1fd#9%1hd&9+jl-i=g1741n_bv#d#e(m-8')

# SECURITY WARNING: don't run with debug turned on in production!
DEBUG = True

ALLOWED_HOSTS = ['*']


DATABASES = {
    'default': {
        'ENGINE': 'django.db.backends.mysql',
        'NAME': 'damda',
        'USER': 'root',
        'PASSWORD': '1324adsf',
        'HOST': '127.0.0.1',
        'PORT': '3306'
    }
}
