function az(){}
function cz(){cz=gAc;bz=new jxc}
function K1b(a,b,c,d){var e;a.a.Eg(b,c);e=M1b(a.a.i,b,c);Be(e,d,true)}
function dz(d,a){var b=d.a;for(var c in b){b.hasOwnProperty(c)&&a.$d(c)}}
function fz(d,a){a=String(a);var b=d.a;var c=b[a];(c==null||!b.hasOwnProperty(a))&&d.Cd(a);return String(c)}
function iz(){cz();var a;a=tP(bz.Vd(eHc),51);if(!a){a=new gz;bz.Xd(eHc,a)}return a}
function ez(c,b){try{typeof $wnd[b]!='object'&&kz(b);c.a=$wnd[b]}catch(a){kz(b)}}
function kz(a){throw new ryc(cCc+a+"' is not a JavaScript object and cannot be used as a Dictionary")}
function gz(){this.b='Dictionary userInfo';ez(this,eHc);if(!this.a){throw new ryc("Cannot find JavaScript object with the name 'userInfo'")}}
function Hob(){var a,b,c,d,e,f,g,i,j,k,n;f=new fgc;g=new I$b('<pre>var userInfo = {\n&nbsp;&nbsp;name: "Amelie Crutcher",\n&nbsp;&nbsp;timeZone: "EST",\n&nbsp;&nbsp;userID: "123",\n&nbsp;&nbsp;lastLogOn: "2/2/2006"\n};<\/pre>\n');g.Q.dir=xBc;g.Q.style['textAlign']=SBc;cgc(f,new I$b('<b>Cet exemple interagit avec le JavaScript variable suivant:<\/b>'));cgc(f,g);j=new B1b;b=j.j;i=iz();e=(n=new sxc,dz(i,n),n);a=0;for(d=$tc(oC(e.a));d.a.ee();){c=tP(fuc(d),1);k=fz(i,c);r1b(j,0,a,c);K1b(b,0,a,'cw-DictionaryExample-header');r1b(j,1,a,k);K1b(b,1,a,'cw-DictionaryExample-data');++a}cgc(f,new I$b('<br><br>'));cgc(f,j);return f}
var eHc='userInfo';_=gz.prototype=az.prototype=new Y;_.gC=function hz(){return yS};_.Cd=function jz(a){var b;b="Cannot find '"+a+"' in "+this;throw new ryc(b)};_.tS=function lz(){return this.b};_.cM={51:1};_.a=null;_.b=null;var bz;_=Lob.prototype;_.ac=function Pob(){d9(this.a,Hob())};var yS=foc(gFc,'Dictionary');yAc(sj)(13);