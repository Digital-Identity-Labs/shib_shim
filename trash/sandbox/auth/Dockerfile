FROM centos:centos7

RUN yum install -y epel-release
RUN yum install -y python-pip
RUN pip install flask redis

ADD auth.py /opt/auth.py

EXPOSE 5000

CMD python /opt/auth.py
