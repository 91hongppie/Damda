from __future__ import absolute_import, unicode_literals

import os
from celery import Celery
from celery.schedules import crontab

#'셀러리' 프로그램을 위해 기본 장고 설정파일을 설정합니다.
os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'damda.settings.local')

from django.conf import settings
from datetime import timedelta

app = Celery('damda_FCM', broker='django://')


#여기서 문자열을 사용하는 것은 워커(worker)가 자식 프로세스로 설정 객체를 직렬화(serialize)하지 않아도 된다는 뜻입니다.  
#뒤에 namespace='CELERY'는 모든 셀러리 관련 설정 키는 'CELERY_' 라는 접두어를 가져야 한다고 알려줍니다.  
app.config_from_object('django.conf:settings', namespace='CELERY')  

#등록된 장고 앱 설정에서 task를 불러옵니다.  
app.autodiscover_tasks(lambda: settings.INSTALLED_APPS)  

app.conf.update(
    BROKER_URL='amqp://',
    CELERY_RESULT_BACKEND = 'django-db',
    CELERY_TASK_SERIALIZER='json',
    CELERY_ACCEPT_CONTENT=['json'],  # Ignore other content
    CELERY_RESULT_SERIALIZER='json',
    CELERY_TIMEZONE='Asia/Seoul',
    CELERY_ENABLE_UTC=False,
    CELERYBEAT_SCHEDULE = {
        'send_push_weekly': {
            'task': 'accounts.tasks.sendPushWeekly',
            'schedule': timedelta(minutes=1),
            'args': ()
        },
    }
)

@app.task(bind=True)
def debug_task(self):
    print('Request: {0!r}'.format(self.request))


if __name__ == '__main__':
    app.start()