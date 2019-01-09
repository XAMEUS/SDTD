<?php

if(isset($_POST["analyzeDate"]) && !empty($_GET["analyzeDate"])) {
    $analyzeDate = $_GET["analyzeDate"];

    $producer = escapeshellcmd("python3 analyze.py $analyzeDate");
    $output = shell_exec($command);

    if($output == null) {
        echo "Error : python3 analyze.py $analyzeDate";
    } else {
        echo "The analyze was successfully launched";
    }
}
