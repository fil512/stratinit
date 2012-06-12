(ns stratinit-install.core
  (:use [clojure.xml]))

(defn parse-file [filename]
    (first (xml-seq (parse filename))))

(defn is-core [string]
  (.contains string "_core"))

(defn is-core-entry [entry]
  (let [filename-attr (-> entry :attrs :fileName )]
    (is-core filename-attr)))