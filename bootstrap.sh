#!/usr/bin/env bash

export DEBIAN_FRONTEND=noninteractive

# All the commands will run as the root by vagrant provisioning
# unless the user is explicitly set.
# Add '/usr/local/bin' to root's $PATH variable.
export PATH=$PATH:/usr/local/bin

# Update
apt-get -q -y autoclean
apt-get -q -y autoremove
apt-get -q -y update
apt-get -q -y upgrade

# Tools
apt-get -q -y install vim bzip2 curl zip git

# NFS Client
apt-get -q -y install nfs-common

# node.js
apt-get -q -y install nodejs
ln -s /usr/bin/nodejs /usr/bin/node

# PhantomJS
apt-get -q -y install fontconfig freetype2-demos
su - vagrant -c "wget https://bitbucket.org/ariya/phantomjs/downloads/phantomjs-1.9.8-linux-x86_64.tar.bz2"
su - vagrant -c "rm -rf phantomjs-1.9.8-linux-x86_64"
su - vagrant -c "bunzip2 phantomjs-1.9.8-linux-x86_64.tar.bz2"
su - vagrant -c "tar xvf phantomjs-1.9.8-linux-x86_64.tar"
rm phantomjs-1.9.8-linux-x86_64.tar

# npm
apt-get -q -y install npm

# bower
npm install -g bower

# grunt
npm install -g grunt-cli

# Sass
apt-get -q -y install ruby
gem install sass

# Java
apt-get -q -y install openjdk-7-jdk

# Play
su - vagrant -c "wget http://downloads.typesafe.com/play/2.2.6/play-2.2.6.zip"
su - vagrant -c "rm -rf play-2.2.6"
su - vagrant -c "unzip play-2.2.6.zip"
rm play-2.2.6.zip

# Redis
apt-get -q -y install redis-server

# .bash_profile
su - vagrant -c "echo 'source ~/.profile' > .bash_profile"
su - vagrant -c "echo 'export PATH=$PATH:~/phantomjs-1.9.8-linux-x86_64/bin:~/play-2.2.6' >> ~/.bash_profile"
su - vagrant -c "echo 'export SBT_OPTS=\"-Xmx2000M -Xss2M -XX:PermSize=128M -XX:MaxPermSize=256M -XX:+UseConcMarkSweepGC -XX:+CMSClassUnloadingEnabled\"' >> ~/.bash_profile"
su - vagrant -c "source ~/.bash_profile"
