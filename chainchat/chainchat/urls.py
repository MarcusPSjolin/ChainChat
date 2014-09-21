from django.conf.urls import patterns, include, url
from . import views
from django.contrib import admin
admin.autodiscover()

urlpatterns = patterns('',
	url(r'^$', views.MyView.as_view()),
    url(r'^admin/', include(admin.site.urls)),
)
