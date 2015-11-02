#!/bin/bash

if [ $# -ne 1 ]; then
    echo -e "Usage:\n\t$0 <tagname>"
    exit
fi

svn cp https://ehu-bio.googlecode.com/svn/trunk/ "https://ehu-bio.googlecode.com/svn/tags/$1"
