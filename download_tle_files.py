import urllib.request as request
import tqdm
import os

tle_dir = './tles'

if not os.path.exists(tle_dir):
    os.makedirs(tle_dir)

with open('./celestrak_all_tle_urls.txt') as urls:
    for url in tqdm.tqdm(urls):
        filename = url.split('/')[-1].strip()
        request.urlretrieve(url, 'tles/' + filename)
