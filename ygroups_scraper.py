'''
Scraper for yahoo groups directory pages
'''

import re
import requests
import time
import urlparse

from BeautifulSoup import BeautifulSoup, SoupStrainer


YAHOO_US_AND_INT_GROUPS_PAGES = [
	"http://groups.yahoo.com/",
	"http://asia.groups.yahoo.com/",
	"http://ar.groups.yahoo.com/",
	"http://au.groups.yahoo.com/",
	"http://br.groups.yahoo.com/",
	"http://ca.groups.yahoo.com/",
	"http://fr.groups.yahoo.com/",
	"http://cf.groups.yahoo.com/",
	"http://de.groups.yahoo.com/",
	"http://hk.groups.yahoo.com/",
	"http://in.groups.yahoo.com/",
	"http://it.groups.yahoo.com/",
	"http://groups.yahoo.co.jp/",
	"http://mx.groups.yahoo.com/",
	"http://es.groups.yahoo.com/",
	"http://uk.groups.yahoo.com/"
]

DIRECTORY_PAGE_URL_REGEX = r'.*/dir/.*'

def fetch_directory_links(target_uri):
	dir_links_only = SoupStrainer('a', href = re.compile(DIRECTORY_PAGE_URL_REGEX))

	content = requests.get(target_uri)
	for link in BeautifulSoup(content.text, parseOnlyThese = dir_links_only):
		resolved_link = urlparse.urljoin(target_uri, link['href'])
		yield resolved_link

if __name__ == '__main__':
	directory_links_queue = YAHOO_US_AND_INT_GROUPS_PAGES

	while directory_links_queue:
		next_in_line = directory_links_queue.pop()
		time.sleep(1)
		print next_in_line
		directory_links_queue.extend(fetch_directory_links(next_in_line))
