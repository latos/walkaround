function bA(){}
function dA(){dA=I1c;cA=new L$c}
function eA(d,a){var b=d.b;for(var c in b){b.hasOwnProperty(c)&&a.$d(c)}}
function vvc(a,b,c,d){var e;a.b.Eg(b,c);e=xvc(a.b.j,b,c);Ce(e,d,true)}
function jA(){dA();var a;a=pgb(cA.Vd(j9c),51);if(!a){a=new hA;cA.Xd(j9c,a)}return a}
function fA(c,b){try{typeof $wnd[b]!='object'&&lA(b);c.b=$wnd[b]}catch(a){lA(b)}}
function lA(a){throw new T_c(G3c+a+"' is not a JavaScript object and cannot be used as a Dictionary")}
function gA(d,a){a=String(a);var b=d.b;var c=b[a];(c==null||!b.hasOwnProperty(a))&&d.Cd(a);return String(c)}
function hA(){this.c='Dictionary userInfo';fA(this,j9c);if(!this.b){throw new T_c("Cannot find JavaScript object with the name 'userInfo'")}}
function CSb(){var a,b,c,d,e,f,g,i,j,k,n;f=new QJc;g=new nsc('<pre>var userInfo = {\n&nbsp;&nbsp;name: "Amelie Crutcher",\n&nbsp;&nbsp;timeZone: "EST",\n&nbsp;&nbsp;userID: "123",\n&nbsp;&nbsp;lastLogOn: "2/2/2006"\n};<\/pre>\n');g.R.dir=$2c;g.R.style['textAlign']=t3c;NJc(f,new nsc('<b>This example interacts with the following JavaScript variable:<\/b>'));NJc(f,g);j=new mvc;b=j.k;i=jA();e=(n=new U$c,eA(i,n),n);a=0;for(d=AXc(ZH(e.b));d.b.ee();){c=pgb(HXc(d),1);k=gA(i,c);cvc(j,0,a,c);vvc(b,0,a,'cw-DictionaryExample-header');cvc(j,1,a,k);vvc(b,1,a,'cw-DictionaryExample-data');++a}NJc(f,new nsc('<br><br>'));NJc(f,j);return f}
var j9c='userInfo';_=hA.prototype=bA.prototype=new Y;_.gC=function iA(){return Kjb};_.Cd=function kA(a){var b;b="Cannot find '"+a+"' in "+this;throw new T_c(b)};_.tS=function mA(){return this.c};_.cM={51:1};_.b=null;_.c=null;var cA;_=GSb.prototype;_.bc=function KSb(){$Cb(this.b,CSb())};var Kjb=HRc(i7c,P4c);$1c(tj)(13);