#!/bin/bash
# Merge master to gh-pages, rebuild the prod cljs, and push.

set -e

git checkout gh-pages
git rebase master
rm resources/public/cljs/main.js
lein cljsbuild once prod
git add -f resources/public/cljs/main.js
git commit -m "Update gh-pages js"
git push
