package main.java.hultberg.sur.Hultberg;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import main.java.hultberg.sur.Hultberg.commands.AddUser;
import main.java.hultberg.sur.Hultberg.commands.AdminChat;
import main.java.hultberg.sur.Hultberg.commands.Ban;
import main.java.hultberg.sur.Hultberg.commands.GlobalChat;
import main.java.hultberg.sur.Hultberg.commands.Group;
import main.java.hultberg.sur.Hultberg.commands.GroupChat;
import main.java.hultberg.sur.Hultberg.commands.HomeCommand;
import main.java.hultberg.sur.Hultberg.commands.Kick;
import main.java.hultberg.sur.Hultberg.commands.LW;
import main.java.hultberg.sur.Hultberg.commands.Low;
import main.java.hultberg.sur.Hultberg.commands.Message;
import main.java.hultberg.sur.Hultberg.commands.ModerateUser;
import main.java.hultberg.sur.Hultberg.commands.Protect;
import main.java.hultberg.sur.Hultberg.commands.Reg;
import main.java.hultberg.sur.Hultberg.commands.ReloadUser;
import main.java.hultberg.sur.Hultberg.commands.SetHomeCommand;
import main.java.hultberg.sur.Hultberg.commands.SetSpawn;
import main.java.hultberg.sur.Hultberg.commands.SpawnCommand;
import main.java.hultberg.sur.Hultberg.commands.SpawnItem;
import main.java.hultberg.sur.Hultberg.commands.StabChat;
import main.java.hultberg.sur.Hultberg.commands.TpCommand;
import main.java.hultberg.sur.Hultberg.commands.TpHereCommand;
import main.java.hultberg.sur.Hultberg.commands.Trade;
import main.java.hultberg.sur.Hultberg.commands.UnProtect;
import main.java.hultberg.sur.Hultberg.commands.Unban;
import main.java.hultberg.sur.Hultberg.commands.WarnCommand;
import main.java.hultberg.sur.Hultberg.commands.WhoCommand;
import main.java.hultberg.sur.Hultberg.handlers.Log;
import main.java.hultberg.sur.Hultberg.handlers.PosBookHandler;
import main.java.hultberg.sur.Hultberg.handlers.blocks.BlockLog;
import main.java.hultberg.sur.Hultberg.handlers.blocks.BlockProtect;
import main.java.hultberg.sur.Hultberg.handlers.groups.GroupHandler;
import main.java.hultberg.sur.Hultberg.handlers.users.BanHandler;
import main.java.hultberg.sur.Hultberg.handlers.users.PlayerData;
import main.java.hultberg.sur.Hultberg.handlers.users.UserHandler;
import main.java.hultberg.sur.Hultberg.handlers.users.WarningsHandler;
import main.java.hultberg.sur.Hultberg.listeners.BlockListener;
import main.java.hultberg.sur.Hultberg.listeners.ChunkListener;
import main.java.hultberg.sur.Hultberg.listeners.EntityListener;
import main.java.hultberg.sur.Hultberg.listeners.PlayerListener;
import main.java.hultberg.sur.Hultberg.listeners.VehicleListener;
import main.java.hultberg.sur.Hultberg.sql.MySQLObject;
import main.java.hultberg.sur.Hultberg.sql.sqlConnector;
import main.java.hultberg.sur.Hultberg.utils.MySQLHandler;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Hultberg extends JavaPlugin {
	
    // Statuser og farger
    public static final int ADMIN = 10;
    public static final ChatColor ADMIN_COLOR = ChatColor.GOLD;
    public static final int MOD = 5;
    public static final ChatColor MOD_COLOR = ChatColor.BLUE;
    public static final int BUILDER = 1;
    public static final ChatColor BUILDER_COLOR = ChatColor.WHITE;
    public static final int GUEST = 0;
    public static final ChatColor GUEST_COLOR = ChatColor.GRAY;
	
	public static Hultberg instance;
	public PluginManager pm;
	
	// SQL
    private sqlConnector sqlConnector;
    private MySQLHandler sqlHandler;
    @SuppressWarnings("unused")
	private MySQLObject mysqlobject;
    private BlockLog blocklog;
    private BlockProtect blockProtect;
	private UserHandler userHandler;
	private GroupHandler groupHandler;
	private PosBookHandler PosBook;
	private Log ch;
	private BanHandler bh;
	private WarningsHandler wh;
	
	// Logger
    public static final Logger log = Logger.getLogger("Minecraft");
	
	public Hultberg() {
        super();

		sqlConnector = new sqlConnector(this);
		sqlHandler = new MySQLHandler(this);
		mysqlobject = new MySQLObject(this);
		userHandler = new UserHandler(this);
		groupHandler = new GroupHandler(this);
		blocklog = new BlockLog(this);
		blockProtect = new BlockProtect(this);
		PosBook = new PosBookHandler(this);
		ch = new Log(this);
		bh = new BanHandler(this);
		wh = new WarningsHandler(this);
    }
	
	@Override
	public void onDisable() {
		this.blockProtect.exit();
        this.blocklog.exit();
        this.bh.exit();
        this.wh.exit();
        this.userHandler.exit();
		Bukkit.getServer().getScheduler().cancelTasks(this);
		log.log(Level.INFO, "[Hultberg] Plugin avsluttet.");
	}
	
	@Override
	public void onEnable() {
		Hultberg.instance = this;
		
		registerListeners();

        sqlConnection();
		// Opprett tabeller som ikke finnes.
        this.sqlHandler.update("CREATE TABLE IF NOT EXISTS `blocklog` (`uid` int(11) NOT NULL,`x` smallint(6) NOT NULL,`y` smallint(6) NOT NULL,`z` smallint(6) NOT NULL,`action` tinyint(1) NOT NULL,`world` varchar(50) NOT NULL,`data` varchar(255) NOT NULL,`time` int(11) NOT NULL) ENGINE=InnoDB DEFAULT CHARSET=latin1");
        this.sqlHandler.update("CREATE TABLE IF NOT EXISTS `blocks` ( `uid` int(11) NOT NULL, `x` int(6) NOT NULL, `y` int(6) NOT NULL, `z` int(6) NOT NULL, `world` varchar(50) NOT NULL, UNIQUE KEY `location` (`x`,`y`,`z`,`world`)) ENGINE=InnoDB DEFAULT CHARSET=latin1");
        this.sqlHandler.update("CREATE TABLE IF NOT EXISTS `log` (`id` int(11) NOT NULL AUTO_INCREMENT,`pid` int(6) NOT NULL,`vid` int(6) NOT NULL,`aid` int(3) NOT NULL,`amount` int(11) NOT NULL DEFAULT '0',`data` varchar(255) NOT NULL,`time` int(11) NOT NULL,PRIMARY KEY (`id`)) ENGINE=MyISAM DEFAULT CHARSET=latin1");
        this.sqlHandler.update("CREATE TABLE IF NOT EXISTS `users` (`id` int(6) NOT NULL AUTO_INCREMENT,`name` varchar(16) NOT NULL,`status` int(2) NOT NULL DEFAULT '0',`active` int(11) NOT NULL,`last_login` int(11) NOT NULL,`groupID` INT( 255 ) NOT NULL,PRIMARY KEY (`id`),UNIQUE KEY `name` (`name`)) ENGINE=MyISAM AUTO_INCREMENT=51 DEFAULT CHARSET=latin1");
        this.sqlHandler.update("CREATE TABLE IF NOT EXISTS `groups` (`gid` int(255) NOT NULL AUTO_INCREMENT,`owner_uid` int(255) NOT NULL,`name` text NOT NULL, `noInv` int(2) NOT NULL, PRIMARY KEY (`gid`)  ) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1;");
        this.sqlHandler.update("CREATE TABLE IF NOT EXISTS `group_invs` (`invid` int(255) NOT NULL AUTO_INCREMENT,`inved_uid` int(255) NOT NULL,`sender_uid` int(255) NOT NULL,`to_gid` int(255) NOT NULL,`accepted` int(2) NOT NULL,PRIMARY KEY (`invid`)) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1;");
        this.sqlHandler.update("CREATE TABLE IF NOT EXISTS `warns` (`wid` int(11) NOT NULL AUTO_INCREMENT,`uid` int(11) NOT NULL,`by` varchar(255) NOT NULL,`reason` mediumtext NOT NULL,`date` varchar(255) NOT NULL,`pos` varchar(255) NOT NULL,PRIMARY KEY (`wid`)) ENGINE=InnoDB DEFAULT CHARSET=utf8;");
        this.sqlHandler.update("CREATE TABLE IF NOT EXISTS `bans` (`ban_id` int(100) NOT NULL AUTO_INCREMENT COMMENT 'Ban id',`banned_user` int(11) NOT NULL COMMENT 'The banned user',`banner` varchar(255) NOT NULL COMMENT 'The banner.',`reason` text NOT NULL COMMENT 'Reason why.',`date` varchar(255) NOT NULL,PRIMARY KEY (`ban_id`)) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;");
        this.sqlHandler.update("INSERT IGNORE INTO `users` SET id='999990', name='Creeper', status='0', active = UNIX_TIMESTAMP(), last_login = UNIX_TIMESTAMP()");
        this.sqlHandler.update("INSERT IGNORE INTO `users` SET id='999991', name='Endermen', status='0', active = UNIX_TIMESTAMP(), last_login = UNIX_TIMESTAMP()");
        this.sqlHandler.update("INSERT IGNORE INTO `users` SET id='999999', name='Server', status='0', active = UNIX_TIMESTAMP(), last_login = UNIX_TIMESTAMP()");
        this.sqlHandler.update("CREATE TABLE IF NOT EXISTS `homes` (`home_id` int(11) NOT NULL AUTO_INCREMENT,`uid` int(11) NOT NULL,`world` varchar(255) NOT NULL,`x` int(11) NOT NULL,`y` int(11) NOT NULL,`z` int(11) NOT NULL,PRIMARY KEY (`home_id`)) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;");
        
        this.userHandler.initialize();
        this.groupHandler.initialize();
        this.blockProtect.initialize();
        this.blocklog.initialize();
        this.bh.initialize();
        this.wh.initialize();
        registerCommands();

        for (Player p : getServer().getOnlinePlayers()) {
            this.userHandler.login(p);
        }
        
		log.log(Level.INFO, "[Hultberg] Plugin lastet.");
	}
	
	public void registerListeners() {
		this.pm = getServer().getPluginManager();
		getServer().getPluginManager().registerEvents(new VehicleListener(this), this);
		getServer().getPluginManager().registerEvents(new ChunkListener(this), this);
		getServer().getPluginManager().registerEvents(new BlockListener(this), this);
		getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
		getServer().getPluginManager().registerEvents(new EntityListener(this), this);
	}
	
	public void registerCommands() {
        getCommand("setspawn").setExecutor(new SetSpawn(this));
        getCommand("tp").setExecutor(new TpCommand(this));
        getCommand("tphere").setExecutor(new TpHereCommand(this));
        getCommand("mod").setExecutor(new ModerateUser(this));
        getCommand("reg").setExecutor(new Reg(this));
        getCommand("msg").setExecutor(new Message(this));
        getCommand("t").setExecutor(new Trade(this));
        getCommand("kick").setExecutor(new Kick(this));
        getCommand("add").setExecutor(new AddUser(this));
        getCommand("ban").setExecutor(new Ban(this));
        getCommand("lw").setExecutor(new LW(this));
        getCommand("low").setExecutor(new Low(this));
        getCommand("group").setExecutor(new Group(this));
        getCommand("gh").setExecutor(new GroupChat(this));
        getCommand("g").setExecutor(new GlobalChat(this));
        getCommand("c").setExecutor(new AdminChat(this));
        getCommand("protect").setExecutor(new Protect(this));
        getCommand("unprotect").setExecutor(new UnProtect(this));
        getCommand("reloaduser").setExecutor(new ReloadUser(this));
        getCommand("i").setExecutor(new SpawnItem(this));
        getCommand("ban").setExecutor(new Ban(this));
        getCommand("unban").setExecutor(new Unban(this));
        getCommand("who").setExecutor(new WhoCommand(this));
        getCommand("sh").setExecutor(new StabChat(this));
        getCommand("home").setExecutor(new HomeCommand(this));
        getCommand("sethome").setExecutor(new SetHomeCommand(this));
        getCommand("low").setExecutor(new Low(this));
        getCommand("warn").setExecutor(new WarnCommand(this));
        getCommand("spawn").setExecutor(new SpawnCommand(this));
    }

	public void broadcastAll(String message) {
        for (World w : this.getServer().getWorlds()) {
            for (Player p : w.getPlayers()) {
                p.sendMessage(message);
            }
        }
        log.log(Level.INFO, message);
    }
    
    public void broadcastAdminChat(String message){
    	for (World w : this.getServer().getWorlds()) {
            for (Player p : w.getPlayers()) {
            	if(this.userHandler.getUserStatus(p) == 5 || this.userHandler.getUserStatus(p) == 10){
            		p.sendMessage("(§6Stab§f/§9Vakt§f) " + message);
            	}
            }
        }
    	log.log(Level.INFO, message);
    }
    
    public void broadcastAdminChatSTAB(String message){
    	for (World w : this.getServer().getWorlds()) {
            for (Player p : w.getPlayers()) {
            	if(this.userHandler.getUserStatus(p) == 10){
            		p.sendMessage("(§6Stab§f) " + message);
            	}
            }
        }
    	log.log(Level.INFO, message);
    }
    
    public void broadcastAdmin(String message){
    	for (World w : this.getServer().getWorlds()) {
            for (Player p : w.getPlayers()) {
            	if(this.userHandler.getUserStatus(p) == 5 || this.userHandler.getUserStatus(p) == 10){
            		p.sendMessage(message);
            	}
            }
        }
    	log.log(Level.INFO, message);
    }
    
    public void broadcastTrade(String message){
    	for (World w : this.getServer().getWorlds()) {
            for (Player p : w.getPlayers()) {
            	p.sendMessage("§a[Handel] §f" + message);
            }
        }
    	log.log(Level.INFO, message);
    }
    
    public void broadcastGroup(String message, int gid){
    	for (World w : this.getServer().getWorlds()) {
            for (Player p : w.getPlayers()) {
            	if(this.userHandler.getGID(p) == gid){
            		p.sendMessage(message);
            	}
            }
        }
    	log.log(Level.INFO, message);
    }
	
	public void sqlConnection() {
        Connection conn = sqlConnector.createConnection();

        if (conn == null) {
            log.log(Level.SEVERE, "[Hultberg] Kunne ikke opprette forbindelse til mysql, disabler plugin.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        } else {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
                log.log(Level.SEVERE, "[Hultberg] Feil under lukking av mysql tilkobling.", e);
            }
        }
        sqlHandler.initialize();
    }

    public MySQLHandler getSqlHandler() {
        return sqlHandler;
    }

    public sqlConnector getSqlConnector() {
        return sqlConnector;
    }
    
    public BlockProtect getBlockProtectHandler() {
        return blockProtect;
    }

    public BlockLog getBlockLogHandler() {
        return blocklog;
    }
	
	public UserHandler getUserHandler() {
        return userHandler;
    }
	
	public GroupHandler getGroupHandler() {
		return groupHandler;
	}
	
	public Player playerMatch(String name) {
        if (this.getServer().getOnlinePlayers().length < 1) {
            return null;
        }

        Player[] online = this.getServer().getOnlinePlayers();
        Player lastPlayer = null;

        for (Player player : online) {
            String playerName = player.getName();
            String playerDisplayName = player.getDisplayName();

            if (playerName.equalsIgnoreCase(name)) {
                lastPlayer = player;
                break;
            } else if (playerDisplayName.equalsIgnoreCase(name)) {
                lastPlayer = player;
                break;
            }

            if (playerName.toLowerCase().indexOf(name.toLowerCase()) != -1) {
                if (lastPlayer != null) {
                    return null;
                }

                lastPlayer = player;
            } else if (playerDisplayName.toLowerCase().indexOf(
                    name.toLowerCase()) != -1) {
                if (lastPlayer != null) {
                    return null;
                }

                lastPlayer = player;
            }
        }

        return lastPlayer;
    }

    public int getUserStatus(String name) {
        PlayerData pd = this.userHandler.getPlayerData(name);
        return pd.getStatus();
    }

    public boolean registerUser(String name) {
        return this.userHandler.register(name);
    }
    
    public boolean setUserStatus(String name, int status) {
        if (this.userHandler.userExists(name)) {
            if (this.userHandler.setStatus(name, status)) {
                this.userHandler.reloadUser(name);
                return true;
            } else {
                return false;
            }
        } else {
            this.userHandler.register(name);
            if (this.userHandler.setStatus(name, status)) {
                this.userHandler.reloadUser(name);
                return true;
            } else {
                return false;
            }
        }
    }

	public PosBookHandler getPosBookHandler() {
		return PosBook;
	}
	
	public Log getLog(){
		return ch;
	}
	
	public BanHandler getBanHandler(){
		return bh;
	}

	public WarningsHandler getWarnHandler() {
		// TODO Auto-generated method stub
		return wh;
	}
	
}