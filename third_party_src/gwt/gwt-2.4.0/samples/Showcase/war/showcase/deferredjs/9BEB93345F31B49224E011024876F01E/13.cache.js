function Ny(){}
function Py(){Py=rzc;Oy=new uwc}
function e1b(a,b,c,d){var e;a.b.Eg(b,c);e=g1b(a.b.j,b,c);Ce(e,d,true)}
function Qy(d,a){var b=d.b;for(var c in b){b.hasOwnProperty(c)&&a.$d(c)}}
function Sy(d,a){a=String(a);var b=d.b;var c=b[a];(c==null||!b.hasOwnProperty(a))&&d.Cd(a);return String(c)}
function Vy(){Py();var a;a=eP(Oy.Vd(rGc),51);if(!a){a=new Ty;Oy.Xd(rGc,a)}return a}
function Ry(c,b){try{typeof $wnd[b]!='object'&&Xy(b);c.b=$wnd[b]}catch(a){Xy(b)}}
function Xy(a){throw new Cxc(pBc+a+"' is not a JavaScript object and cannot be used as a Dictionary")}
function Ty(){this.c='Dictionary userInfo';Ry(this,rGc);if(!this.b){throw new Cxc("Cannot find JavaScript object with the name 'userInfo'")}}
function lob(){var a,b,c,d,e,f,g,i,j,k,n;f=new zfc;g=new YZb('<pre>var userInfo = {\n&nbsp;&nbsp;name: "Amelie Crutcher",\n&nbsp;&nbsp;timeZone: "EST",\n&nbsp;&nbsp;userID: "123",\n&nbsp;&nbsp;lastLogOn: "2/2/2006"\n};<\/pre>\n');g.R.dir=JAc;g.R.style['textAlign']=cBc;wfc(f,new YZb('<b>Cet exemple interagit avec le JavaScript variable suivant:<\/b>'));wfc(f,g);j=new X0b;b=j.k;i=Vy();e=(n=new Dwc,Qy(i,n),n);a=0;for(d=jtc(_B(e.b));d.b.ee();){c=eP(qtc(d),1);k=Sy(i,c);N0b(j,0,a,c);e1b(b,0,a,'cw-DictionaryExample-header');N0b(j,1,a,k);e1b(b,1,a,'cw-DictionaryExample-data');++a}wfc(f,new YZb('<br><br>'));wfc(f,j);return f}
var rGc='userInfo';_=Ty.prototype=Ny.prototype=new Y;_.gC=function Uy(){return lS};_.Cd=function Wy(a){var b;b="Cannot find '"+a+"' in "+this;throw new Cxc(b)};_.tS=function Yy(){return this.c};_.cM={51:1};_.b=null;_.c=null;var Oy;_=pob.prototype;_.bc=function tob(){J8(this.b,lob())};var lS=qnc(vEc,'Dictionary');Jzc(tj)(13);