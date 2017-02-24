#!/bin/bash
# Created: 28.09.2016
# Vladimir Vons, VladVons@gmail.com

Proj="${PWD##*/}"
#Proj="apk_airplan"

Clean()
{
  echo "delete objects"
}

GitAuth()
{
  # sign with eMail
  git config --global user.email "vladvons@gmail.com"

  # no password 
  git config --global credential.helper 'cache --timeout=36000'
}

GitCreate()
{
  # create new project on disk
  git init
  GitAuth

  # remote git server location
  git remote add origin https://github.com/VladVons/$Proj.git

}

GitClone()
{
  # restore clone copy fromserver to disk 
  git clone https://github.com/VladVons/$Proj.git
  GitAuth

  #web admin access here
  #https://github.com/VladVons/appman
}


GitSyncToServ()
# sync only changes from disk to server 
{
  git status

  #git add install.sh
  #git rm TestClient.py
  #git mv README.md README
  #git log

  git add -u -v
  git commit -a -m "just commit"
  git push -u origin master
}

GitFromServ()
# sync changes from server to disk
{
  git pull
}

GitToServ()
# sync changes from disk to serv
{
  Clean
  # add all new files
  git add -A -v
  GitSyncToServ
}


#GitUpdate

clear
case $1 in
    Clean)              "$1"        "$2" "$3" ;;
    GitAuth)            "$1"        "$2" "$3" ;;
    GitCreate)          "$1"        "$2" "$3" ;;
    GitToServ|t)        GitToServ   "$2" "$3" ;;
    GitFromServ|f)      GitFromServ "$2" "$3" ;;
    GitClone)           "$1"        "$2" "$3" ;;
esac

