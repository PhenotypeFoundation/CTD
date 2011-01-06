/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */




function exampleJSON(){

    var url = "http://nbx13.nugo.org/ctd/getJsonExpressionByProbeSetId.jsp?probeSetId=100012_at&password=secret";
    var objJSON = new ActiveXObject("Microsoft.XMLHTTP");
    objJSON.open("GET",url,false);
    objJSON.send();
    var json = objJSON.responseText;

    var data = eval(json);

    for(var i = data.length - 1; i >= 0; --i) {
        var o = data[i];
        var log2value = o.log2Value;
        var accession = o.localAccession;
        var password = o.ticketPassword;
        alert(accession + " " + log2value + " " + password);
    }

}


