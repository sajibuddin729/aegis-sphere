// SPDX-License-Identifier: Apache-2.0
pragma solidity ^0.8.11;

/// @title Aegis-Sphere blockchain integration smoke test
/// @notice Used only to verify contract deployment, reads, writes and events.
contract AegisHello {
    string private message;

    event MessageUpdated(
        string oldMessage,
        string newMessage,
        address indexed caller
    );

    constructor() {
        message = "Aegis-Sphere Phase B";
    }

    function getMessage() external view returns (string memory) {
        return message;
    }

    function setMessage(string calldata newMessage) external {
        string memory oldMessage = message;
        message = newMessage;
        emit MessageUpdated(
            oldMessage,
            newMessage,
            msg.sender
        );
    }
}
