(ns nabble-scraper.core
  (:gen-class :main true)
  (:require [warc-clojure.core :as warc]
            [clojure.tools.cli :as cli]
            [clojure.java.io :as io]
            [net.cgrand.enlive-html :as html])
  (:use [clojure.string :only (join split)]))

(defn extract-forum-links
  [page-html]
  (map second (filter
					(fn
						[x]
					  	(= (first x) "forum"))
					(map 
					   	(fn [site-row-contents] 
					      	(let [contents (html/select site-row-contents #{[:div.tag] [:div.second-font :a]})
					            webapp-type (first (:content (first contents)))
					            link (:href (:attrs (second contents)))]
					    		[webapp-type link]))        
					   	(html/select page-html [:div.site-row])))))

(defn extract-503-forums-list
  "A bunch of forums 503ed for us. Re-initiate a new crawl"
  [seeds-report]
  (with-open [wrtr (io/writer "data/remaining-forums.txt" :append true)]
    (binding [*out* wrtr]
  		(doseq [l (filter #(. % startsWith "503 ") (line-seq (io/reader seeds-report)))]
    		(println (nth (split l #"\s+") 2))))))

(defn -main
  [& args]
  (let [[args [warc-file-name] banner] (cli/cli args)]
    (doseq [record (warc/get-http-records-seq (warc/get-warc-reader warc-file-name))]
      		(doseq [link (extract-forum-links (html/html-resource (io/reader (:payload-stream record))))]
          			(println link)))))