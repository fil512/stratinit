(ns stratinit-install.test.core
  (:use [stratinit-install.core])
  (:use [clojure.test]))

(deftest can-parse
  (let [xml (parse-file "../stratinit-client/target/install4j/updates.xml")]
    (is (= (:tag xml) :updateDescriptor))))

(deftest iscore
  (is (is-core "foo_corebar")))

(deftest isnotcore
  (not (is-core "foo_borebar")))

(deftest first-is-not-core
  (let [xml (parse-file "../stratinit-client/target/install4j/updates.xml")]
    (not (is-core-entry (-> xml :content first)))))

(deftest second-is-not-core
  (let [xml (parse-file "../stratinit-client/target/install4j/updates.xml")]
    (not (is-core-entry (-> xml :content second)))))

(deftest third-is-core
  (let [xml (parse-file "../stratinit-client/target/install4j/updates.xml")]
    (is (is-core-entry (nth (:content xml) 2 )))))

(run-tests);;