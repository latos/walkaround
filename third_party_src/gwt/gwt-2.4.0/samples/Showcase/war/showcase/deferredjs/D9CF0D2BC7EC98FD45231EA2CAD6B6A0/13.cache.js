function oz(){}
function qz(){qz=kAc;pz=new nxc}
function N1b(a,b,c,d){var e;a.b.Ig(b,c);e=P1b(a.b.j,b,c);Pe(e,d,true)}
function rz(d,a){var b=d.b;for(var c in b){b.hasOwnProperty(c)&&a.ce(c)}}
function tz(d,a){a=String(a);var b=d.b;var c=b[a];(c==null||!b.hasOwnProperty(a))&&d.Gd(a);return String(c)}
function wz(){qz();var a;a=HP(pz.Zd(pHc),51);if(!a){a=new uz;pz._d(pHc,a)}return a}
function sz(c,b){try{typeof $wnd[b]!='object'&&yz(b);c.b=$wnd[b]}catch(a){yz(b)}}
function yz(a){throw new vyc(hCc+a+"' is not a JavaScript object and cannot be used as a Dictionary")}
function uz(){this.c='Dictionary userInfo';sz(this,pHc);if(!this.b){throw new vyc("Cannot find JavaScript object with the name 'userInfo'")}}
function Rob(){var a,b,c,d,e,f,g,i,j,k,n;f=new ggc;g=new L$b('<pre>var userInfo = {\n&nbsp;&nbsp;name: "Amelie Crutcher",\n&nbsp;&nbsp;timeZone: "EST",\n&nbsp;&nbsp;userID: "123",\n&nbsp;&nbsp;lastLogOn: "2/2/2006"\n};<\/pre>\n');g.R.dir=BBc;g.R.style['textAlign']=WBc;dgc(f,new L$b('<b>Cet exemple interagit avec le JavaScript variable suivant:<\/b>'));dgc(f,g);j=new E1b;b=j.k;i=wz();e=(n=new wxc,rz(i,n),n);a=0;for(d=cuc(CC(e.b));d.b.ie();){c=HP(juc(d),1);k=tz(i,c);u1b(j,0,a,c);N1b(b,0,a,'cw-DictionaryExample-header');u1b(j,1,a,k);N1b(b,1,a,'cw-DictionaryExample-data');++a}dgc(f,new L$b('<br><br>'));dgc(f,j);return f}
var pHc='userInfo';_=uz.prototype=oz.prototype=new Y;_.gC=function vz(){return QS};_.Gd=function xz(a){var b;b="Cannot find '"+a+"' in "+this;throw new vyc(b)};_.tS=function zz(){return this.c};_.cM={51:1};_.b=null;_.c=null;var pz;_=Vob.prototype;_.fc=function Zob(){n9(this.b,Rob())};var QS=joc(tFc,'Dictionary');CAc(Hj)(13);