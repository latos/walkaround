function xz(){}
function zz(){zz=EAc;yz=new Hxc}
function Y1b(a,b,c,d){var e;a.b.Mg(b,c);e=$1b(a.b.j,b,c);Qe(e,d,true)}
function Az(d,a){var b=d.b;for(var c in b){b.hasOwnProperty(c)&&a.ge(c)}}
function Cz(d,a){a=String(a);var b=d.b;var c=b[a];(c==null||!b.hasOwnProperty(a))&&d.Kd(a);return String(c)}
function Fz(){zz();var a;a=QP(yz.be(GHc),51);if(!a){a=new Dz;yz.de(GHc,a)}return a}
function Bz(c,b){try{typeof $wnd[b]!='object'&&Hz(b);c.b=$wnd[b]}catch(a){Hz(b)}}
function Hz(a){throw new Pyc(GCc+a+"' is not a JavaScript object and cannot be used as a Dictionary")}
function Dz(){this.c='Dictionary userInfo';Bz(this,GHc);if(!this.b){throw new Pyc("Cannot find JavaScript object with the name 'userInfo'")}}
function dpb(){var a,b,c,d,e,f,g,i,j,k,n;f=new ugc;g=new W$b('<pre>var userInfo = {\n&nbsp;&nbsp;name: "Amelie Crutcher",\n&nbsp;&nbsp;timeZone: "EST",\n&nbsp;&nbsp;userID: "123",\n&nbsp;&nbsp;lastLogOn: "2/2/2006"\n};<\/pre>\n');g.R.dir=bCc;g.R.style['textAlign']=tCc;rgc(f,new W$b('<b>Cet exemple interagit avec le JavaScript variable suivant:<\/b>'));rgc(f,g);j=new P1b;b=j.k;i=Fz();e=(n=new Qxc,Az(i,n),n);a=0;for(d=wuc(LC(e.b));d.b.me();){c=QP(Duc(d),1);k=Cz(i,c);F1b(j,0,a,c);Y1b(b,0,a,'cw-DictionaryExample-header');F1b(j,1,a,k);Y1b(b,1,a,'cw-DictionaryExample-data');++a}rgc(f,new W$b('<br><br>'));rgc(f,j);return f}
var GHc='userInfo';_=Dz.prototype=xz.prototype=new Y;_.gC=function Ez(){return aT};_.Kd=function Gz(a){var b;b="Cannot find '"+a+"' in "+this;throw new Pyc(b)};_.tS=function Iz(){return this.c};_.cM={51:1};_.b=null;_.c=null;var yz;_=hpb.prototype;_.fc=function lpb(){B9(this.b,dpb())};var aT=Doc(LFc,'Dictionary');WAc(Hj)(13);