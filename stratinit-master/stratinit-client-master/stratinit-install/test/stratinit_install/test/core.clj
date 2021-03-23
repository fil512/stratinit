(ns stratinit-install.test.core
  (:use [stratinit-install.core])
  (:use [clojure.test]))

(deftest can-parse
  (let [xml (parse-file "../stratinit-swt/target/install4j/updates.xml")]
    (is (= (:tag xml) :updateDescriptor))))

(deftest iscore
  (is (is-core "foo_corebar")))

(deftest isnotcore
  (not (is-core "foo_borebar")))

(deftest first-is-not-core
  (let [xml (parse-file "../stratinit-swt/target/install4j/updates.xml")]
    (is (is-not-core-entry (-> xml :content first)))))

(deftest second-is-not-core
  (let [xml (parse-file "../stratinit-swt/target/install4j/updates.xml")]
    (is (is-not-core-entry (-> xml :content second)))))

(deftest third-is-core
  (let [xml (parse-file "../stratinit-swt/target/install4j/updates.xml")]
    (not (is-not-core-entry (nth (:content xml) 2 )))))

(deftest filter-entries
  (let [xml (parse-file "../stratinit-swt/target/install4j/updates.xml")]
    (is (= (count (:content xml)) 9))
    (is (= (count (:content (filter-core-out xml))) 5))
    ))

(deftest fix-executable-test
  (is (= (fix-executable "stratinit_windows_1_2_5.exe") "stratinit_windows_1_2_5_core.exe"))
  (is (= (fix-executable "stratinit_unix32_1_2_5.sh") "stratinit_unix_1_2_5_core.sh"))
  (is (= (fix-executable "stratinit_unix64_1_2_5.sh") "stratinit_unix_1_2_5_core.sh"))
  (is (= (fix-executable "stratinit_macos_1_2_5.dmg") "stratinit_macos_1_2_5_core.dmg"))
  )

(run-tests)
