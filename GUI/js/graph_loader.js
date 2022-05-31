/*

Dans ce programme on contruit tout les graphiques utiles pour la visualisation de nos noeuds

On se sert de 2 librairies de modélisation de graph 2D et 3D
https://github.com/vasturiano

*/


//Exemple de données reçus par l'API
var myData = {
    "nodes": [
        {
          "id": "192.168.1.1",
          "name": "192.168.1.1:33445",
          "val": 0
        },
        {
          "id": "192.168.1.2",
          "name": "192.168.1.2:33445",
          "val": 0
        },
        {
          "id": "192.168.1.3",
          "name": "192.168.1.3:2230",
          "val": 0
        },
        {
          "id": "192.168.1.4",
          "name": "192.168.1.4:33445",
          "val": 0
        },
        {
          "id": "192.168.1.5",
          "name": "192.168.1.5:33445",
          "val": 0
        },
        {
          "id": "192.168.1.6",
          "name": "192.168.1.6:33445",
          "val": 0
        },
        {
          "id": "192.168.1.7",
          "name": "192.168.1.7:33445",
          "val": 0
        },
        {
          "id": "192.168.1.8",
          "name": "192.168.1.8:33445",
          "val": 0
        },
        {
          "id": "192.168.1.9",
          "name": "192.168.1.9:33445",
          "val": 0
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
        },
        {
          "source": "192.168.1.1",
          "target": "192.168.1.6"
        },
        {
          "source": "192.168.1.2",
          "target": "192.168.1.7"
        },
        {
          "source": "192.168.1.2",
          "target": "192.168.1.9"
        },
        {
          "source": "192.168.1.3",
          "target": "192.168.1.5"
        },
        {
          "source": "192.168.1.3",
          "target": "192.168.1.6"
      },
      {
        "source": "192.168.1.4",
        "target": "192.168.1.9"
      },
      {
        "source": "192.168.1.4",
        "target": "192.168.1.2"
      },
      {
        "source": "192.168.1.5",
        "target": "192.168.1.7"
      },
      {
        "source": "192.168.1.5",
        "target": "192.168.1.8"
      },
      {
        "source": "192.168.1.6",
        "target": "192.168.1.1"
      },
      {
        "source": "192.168.1.6",
        "target": "192.168.1.2"
    },
    {
      "source": "192.168.1.4",
      "target": "192.168.1.2"
    },
    {
      "source": "192.168.1.8",
      "target": "192.168.1.5"
    },
    {
      "source": "192.168.1.9",
      "target": "192.168.1.2"
    },
    {
      "source": "192.168.1.4",
      "target": "192.168.1.2"
    },
    {
      "source": "192.168.1.7",
      "target": "192.168.1.8"
    },
    {
      "source": "192.168.1.6",
      "target": "192.168.1.7"
    }
    ]
}

//On calcule les valeurs des poids qui seront stockés dans le champs "val"
for (let i = 0; i < myData["nodes"].length; i++) {
  var node = myData["nodes"][i];
  var val = 0;
  for (let j=0; j<myData["links"].length; j++){
    if (myData["links"][j]["source"] == node["id"] || myData["links"][j]["target"] == node["id"]){
      val+=2;
    }
  }
  node["val"] = val;
}



//On récupère la taille de l'écran
const main_panel = document.getElementById('main_panel');
var height = main_panel.clientHeight*(2/3);
var width = main_panel.clientWidth*(2/3);

//On contruit le 3D graph
const div_3d = document.getElementById('forced-graph-3d');
var myGraph_3d = ForceGraph3D()(div_3d)
  .width(width)
  .height(height)
  .backgroundColor('#101020')
  .nodeAutoColorBy('id')
  .linkColor(() => 'rgba(255,255,255,1)')
  .graphData(myData);

//On construit le 2D graph
const elem = document.getElementById('forced-graph-2d');
const Graph = ForceGraph()(elem)
  .backgroundColor('#101020')
  .nodeRelSize(2)
  .nodeAutoColorBy('id')
  .linkColor(() => 'rgba(255,255,255,0.2)')
  .linkDirectionalParticles(1)
  .width(width)
  .height(height)
  .graphData(myData);


//On charge la table
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
      text_target = link["target"] + " " + text_target;
    }
  }
  var cellText_target = document.createTextNode(text_target);
  var row_target = document.createElement("td");
  row_target.appendChild(cellText_target);
  new_line.appendChild(row_target);


  labels_line.after(new_line);
};


