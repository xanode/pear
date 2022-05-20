/*

Ce programme sert à créer les interactions avec les différents boutons de la page

*/

//sideNav buttons
const graph_3d = document.getElementById('forced-graph-3d');
const graph_2d = document.getElementById('forced-graph-2d');
const tableau = document.getElementById('tableau');
const toggle_3d = document.getElementById('toggle-3d');

function active_network(){
  toggle_3d.classList.remove('inactive');
  graph_2d.classList.remove('inactive');
  tableau.classList.add('inactive');
}
function active_home(){
  toggle_3d.classList.add('inactive');
  graph_3d.classList.add('inactive');
  graph_2d.classList.add('inactive');
  tableau.classList.remove('inactive');
}



//toggle 2d to 3d
var active_2d = false;
function toggle_2d() {
  if (active_2d){
    graph_3d.classList.add('inactive');
    graph_2d.classList.remove('inactive');
    active_2d = false;
  } else {
    graph_2d.classList.add('inactive');
    graph_3d.classList.remove('inactive');
    active_2d = true;
  }
}


