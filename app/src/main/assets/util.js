/*
 * InfoLey 4.2 - 2024 
 * Copyright (C) 2017-2024 Carlos A. Martínez 
 * 
 * email: infoleyhys@gmail.com
 * web: consultoramartinez.com.ar
 * 
 * Rosario, Argentina.
 */

/*
* en algun momento se pensó en utilizar graficos SVG y mostrar PNG en telefonos no compatibles, pero no se implementó
*
function hideSVG() {
var items = document.querySelectorAll('.svg_container');
for(index in items) {
items[index].style.display = 'none';
                    }
                    }
function showPNG() {
 var items = document.querySelectorAll('.png_container');
  for(index in items) {
  items[index].style.display = 'block';
       }
}*/


function changeFontColor(color)
{
document.body.style.color = color;
}

function writeTopPageTitle()
{
document.getElementById("PAGENAME").innerHTML = document.querySelector("meta[name=description]").getAttribute("content");
}