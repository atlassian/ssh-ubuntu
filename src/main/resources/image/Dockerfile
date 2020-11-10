FROM ubuntu:UBUNTU_CODENAME

RUN echo "deb mirror://mirrors.ubuntu.com/mirrors.txt UBUNTU_CODENAME main restricted universe multiverse\n" > /etc/apt/sources.list
RUN apt-get update
RUN apt-get -y upgrade

RUN apt-get install --no-install-recommends -y openssh-server ca-certificates

RUN echo 'root:root' |chpasswd

RUN sed -ri 's/^#?PermitRootLogin\s+.*/PermitRootLogin yes/' /etc/ssh/sshd_config
RUN sed -ri 's/UsePAM yes/#UsePAM yes/g' /etc/ssh/sshd_config

RUN mkdir /root/.ssh

RUN apt-get clean && rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*


COPY authorized_keys /root/.ssh/

RUN mkdir /var/run/sshd

EXPOSE 22

CMD    ["/usr/sbin/sshd", "-D"]
