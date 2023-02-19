package main

import (
	"fmt"
	"log"
	"os"
	"os/signal"
	"path/filepath"
	"time"

	"github.com/hacdias/go-libp2p-pnet"
	"github.com/mdlayher/samba"
)

func main() {
	// Set up the Samba server configuration
	conf := samba.NewConfig()

	// Configure the server to use the encrypted transport provided by the go-libp2p-pnet library
	// First, create a new keypair using the Curve25519 elliptic curve.
	// This keypair will be used to encrypt and decrypt all Samba traffic.
	keypair, err := pnet.GenerateKeypair(pnet.Curve25519)
	if err != nil {
		log.Fatalf("Failed to generate keypair: %s", err)
	}

	// Set the server's encryption keypair to the one we just generated.
	conf.EncryptionKey = keypair

	// Set the server's encryption mode to "required", which means all clients must use encryption.
	conf.EncryptionMode = samba.EncryptionModeRequired

	// Set the server's workgroup and server name.
	conf.Workgroup = "WORKGROUP"
	conf.ServerString = "My Samba Server"

	// Set the server's default file creation mask.
	conf.FileCreateMask = 0666

	// Set the server's default directory creation mask.
	conf.DirectoryCreateMask = 0777

	// Set the server's root directory.
	root := "/srv/samba"
	if err := os.MkdirAll(root, 0777); err != nil {
		log.Fatalf("Failed to create root directory: %s", err)
	}

	// Create the server and start it.
	server := samba.NewServer(conf)
	if err := server.ListenAndServe(filepath.Join(root, "smb.conf")); err != nil {
		log.Fatalf("Failed to start Samba server: %s", err)
	}

	// Wait for an interrupt signal to shut down the server.
	sig := make(chan os.Signal, 1)
	signal.Notify(sig, os.Interrupt)
	<-sig

	log.Println("Shutting down Samba server...")

	// Shut down the server gracefully.
	server.GracefulStop(time.Second * 5)

	log.Println("Samba server shut down.")
}
