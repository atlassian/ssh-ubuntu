FROM rastasheep/ubuntu-sshd:18.04

RUN echo "deb mirror://mirrors.ubuntu.com/mirrors.txt bionic main restricted universe multiverse\n" > /etc/apt/sources.list
COPY authorized_keys /root/.ssh/