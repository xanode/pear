//Data
var myData = {
    "nodes": [
        {
          "id": "192.168.1.1",
          "name": "192.168.1.1:2200",
          "val": 2
        },
        {
          "id": "192.168.1.2",
          "name": "192.168.1.2:2200",
          "val": 1
        },
        {
          "id": "192.168.1.3",
          "name": "192.168.1.3:2230",
          "val": 1
        }
    ],
    "links": [
        {
            "source": "192.168.1.1",
            "target": "192.168.1.2"
        },
        {
          "source": "192.168.1.1",
          "target": "192.168.1.3"
        }
    ]
}


//size
const main_panel = document.getElementById('main_panel');
var height = main_panel.clientHeight*(2/3);
var width = main_panel.clientWidth*(2/3);

//3D graph
const div_3d = document.getElementById('forced-graph-3d');
var myGraph_3d = ForceGraph3D()(div_3d)
.width(width)
.height(height)
.graphData(myData);

//2D graph
const elem = document.getElementById('forced-graph-2d');
const Graph = ForceGraph()(elem)
  .backgroundColor('#101020')
  .nodeRelSize(6)
  .nodeAutoColorBy('user')
  .linkColor(() => 'rgba(255,255,255,0.2)')
  .linkDirectionalParticles(1)
  .width(width)
  .height(height)
  .graphData(myData);




//load table
const labels_line = document.getElementById("labels");
for (let i = 0; i < myData["nodes"].length; i++) {
  //Création d'une nouvelle ligne
  var new_line = document.createElement("tr");

  //On travaille sur un seul noeud
  const node = myData["nodes"][i];

  //On récupère l'ip
  var row_ip = document.createElement("td");
  row_ip.appendChild(document.createTextNode(node["id"]));
  new_line.appendChild(row_ip);

  //le port
  var row_port = document.createElement("td");
  var port = node["name"].slice(node["name"].search(":")+1);
  row_port.appendChild(document.createTextNode(port));
  new_line.appendChild(row_port);

  //La valeur
  var row_val = document.createElement("td");
  row_val.appendChild(document.createTextNode(node["val"]));
  new_line.appendChild(row_val);

  //On s'occupe de récupérer les targets
  var text_target = "";
  for (let j=0; j<myData["links"].length; j++){
    var link = myData["links"][j];
    if (link["source"] == node["id"]){
      text_target = link["target"] + " -- " + text_target;
    }
  }
  var cellText_target = document.createTextNode(text_target);
  var row_target = document.createElement("td");
  row_target.appendChild(cellText_target);
  new_line.appendChild(row_target);


  labels_line.after(new_line);
};


