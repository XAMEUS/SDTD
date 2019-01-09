Les identifiants de connexion à AWS peuvent se configurer dans le fichier `~/.aws/credentials`
```
[default]
aws_access_key_id=KEY_ID
aws_secret_access_key=ACCESS_KEY
```

Dans un premier il faut créer une clé pour pouvoir accéder plus tard en ssh aux instances déployées.

```
python3 keys.py
```

Pour créer des instances:
```
python3 create.py
```

Pour voir les intances:
```
python3 instances.py
```

Pour se connecter (en donnant le fichier généré par `keys.py`):
```
ssh -i key.pem ubuntu@<ip>
```

Pour détruire les instances:
```
python3 terminate.py
```
